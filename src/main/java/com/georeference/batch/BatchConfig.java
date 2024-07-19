package com.georeference.batch;

import com.georeference.batch.listeners.BatchStepExecutionListener;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.repositories.GeoreferenceRecordFailRepository;
import com.georeference.process.repositories.GeoreferenceRecordRepository;
import com.georeference.utils.BatchException;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    @Qualifier("dataSource")
    private final DataSource dataSource;

    private final GeoreferenceRecordProcessor georeferenceRecordProcessor;

    @Autowired
    private GeoreferenceRecordRepository georeferenceRecordRepository;

    @Autowired
    private GeoreferenceRecordFailRepository georeferenceRecordFailRepository;

    @Qualifier("mainEntityManagerFactory")
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Autowired
    public BatchConfig(GeoreferenceRecordProcessor georeferenceRecordProcessor, LocalContainerEntityManagerFactoryBean entityManagerFactory, DataSource dataSource) {
        this.georeferenceRecordProcessor = georeferenceRecordProcessor;
        this.entityManagerFactory = entityManagerFactory;
        this.dataSource = dataSource;
    }

    private static final String[] FIELDS = new String[] {
            //"index",
            "farmerName", "documentType", "documentNumber",
            "farmName", "cultivationArea",
            "municipalityCode", "municipalityName", "departmentCode",
            "departmentName"//, "status" ,"geoJsonId", "oldPlot"
    };

    /**
     * Georeference request batch settings
     */

    @Bean
    public FlatFileItemReader<GeoreferenceRecord> reader() {
        return new FlatFileItemReaderBuilder<GeoreferenceRecord>()
                //.resource(new FileSystemResource(fileName))
                .encoding("UTF-8")
                .linesToSkip(1)
                .name("csvItemReader")
                .strict(false)
                .lineTokenizer(tokenizer())
                .fieldSetMapper(fieldSet -> {
                    return GeoreferenceRecord.builder()
                            .farmerName(fieldSet.readString("farmerName"))
                            .documentType(fieldSet.readString("documentType"))
                            .documentNumber(fieldSet.readInt("documentNumber"))
                            .farmName(fieldSet.readString("farmName"))
                            .cultivationArea(fieldSet.readDouble("cultivationArea"))
                            .municipalityCode(fieldSet.readString("municipalityCode"))
                            .municipalityName(fieldSet.readString("municipalityName"))
                            .departmentCode(fieldSet.readString("departmentCode"))
                            .departmentName(fieldSet.readString("departmentName"))
                            //.status(fieldSet.readString("status"))
                            //.geoJsonId(fieldSet.readString("geoJsonId"))
                            //.oldPlot(fieldSet.readBoolean("oldPlot"))
                            .build();
                })
                .build();
    }

    @Bean
    public ItemWriter<GeoreferenceRecord> writer() {
        return new ItemWriter<GeoreferenceRecord>() {
            @Override
            public void write(Chunk<? extends GeoreferenceRecord> chunk) throws Exception {
                if (georeferenceRecordProcessor.getErrorList().isEmpty()) {
                    List<GeoreferenceRecord> records = new ArrayList<>();
                    for (GeoreferenceRecord item : chunk.getItems()) {
                        GeoreferenceRecord georeferenceRecord = getGeoreferenceRecord(item);
                        records.add(georeferenceRecord);
                    }
                    georeferenceRecordRepository.saveAll(records);
                } else {
                    georeferenceRecordFailRepository.saveAll(georeferenceRecordProcessor.getErrorList());
                    throw new BatchException("Errors found during processing. Transaction rolled back.");
                }
            }
        };
                //.usePersist(true)
                /*.entityManagerFactory(entityManagerFactory.getObject())
                .build();*/
    }

    private static GeoreferenceRecord getGeoreferenceRecord(GeoreferenceRecord item) {
        GeoreferenceRecord georeferenceRecord = new GeoreferenceRecord();
        georeferenceRecord.setFarmerName(item.getFarmerName());
        georeferenceRecord.setDocumentType(item.getDocumentType());
        georeferenceRecord.setDocumentNumber(item.getDocumentNumber());
        georeferenceRecord.setFarmName(item.getFarmName());
        georeferenceRecord.setCultivationArea(item.getCultivationArea());
        georeferenceRecord.setMunicipalityCode(item.getMunicipalityCode());
        georeferenceRecord.setMunicipalityName(item.getMunicipalityName());
        georeferenceRecord.setDepartmentCode(item.getDepartmentCode());
        georeferenceRecord.setDepartmentName(item.getDepartmentName());
        return georeferenceRecord;
    }

    @Bean(name = "csvImporterStep")
    public Step csvImporterStep(/*ItemReader<GeoreferenceRecord> reader, ItemWriter<GeoreferenceRecord> writer, JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,*/) throws Exception {
        return new StepBuilder("csvImporterStep", jobRepository())
                .<GeoreferenceRecord, GeoreferenceRecord>chunk(5, platformTransactionManager())
                .reader(reader())
                .writer(writer())
                .listener(new BatchStepExecutionListener(reader()))
                .processor(georeferenceRecordProcessor)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(platformTransactionManager());
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "csvImporterJob")
    public Job csvImporterJob(Step csvImporterStep, JobRepository jobRepository) throws Exception {
        return new JobBuilder("csvImporterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                //.listener()
                //.validator(parameters -> System.out.println(parameters.getLong("requestId")))
                .flow(csvImporterStep())
                .end()
                .build();
    }

    @Bean
    public DefaultLineMapper<GeoreferenceRecord> lineMapper(LineTokenizer tokenizer, FieldSetMapper<GeoreferenceRecord> fieldSetMapper) {
        var lineMapper = new DefaultLineMapper<GeoreferenceRecord>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public BeanWrapperFieldSetMapper<GeoreferenceRecord> fieldSetMapper() {
        var fieldSetMapper = new BeanWrapperFieldSetMapper<GeoreferenceRecord>();
        fieldSetMapper.setTargetType(GeoreferenceRecord.class);
        return fieldSetMapper;
    }

    @Bean
    public DelimitedLineTokenizer tokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames(FIELDS);
        return tokenizer;
    }


}
