package com.georeference.batch;

import com.georeference.batch.listeners.BatchJobExecuteListener;
import com.georeference.batch.listeners.BatchStepExecutionListener;
import com.georeference.batch.processors.GeoreferenceRecordProcessor;
import com.georeference.process.entities.GeoreferenceRecord;
import com.georeference.process.repositories.GeoreferenceRecordFailRepository;
import com.georeference.process.repositories.GeoreferenceRecordRepository;
import com.georeference.services.FileService;
import com.georeference.services.GeoreferenceRequestService;
import com.georeference.services.sica.producers.SicaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableBatchProcessing
@Slf4j
//@ComponentScan(basePackages = {"com.georeference.batch.processors"})
public class BatchConfig {

    @Qualifier("dataSource")
    private final DataSource dataSource;

    private final GeoreferenceRecordProcessor georeferenceRecordProcessor;
    private final GeoreferenceRecordRepository georeferenceRecordRepository;
    private final GeoreferenceRecordFailRepository georeferenceRecordFailRepository;

    @Qualifier("mainEntityManagerFactory")
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;

    //@Autowired
    public BatchConfig(GeoreferenceRecordRepository georeferenceRecordRepository, GeoreferenceRecordFailRepository georeferenceRecordFailRepository, GeoreferenceRecordProcessor georeferenceRecordProcessor, LocalContainerEntityManagerFactoryBean entityManagerFactory, DataSource dataSource) {
        this.georeferenceRecordRepository = georeferenceRecordRepository;
        this.georeferenceRecordFailRepository  = georeferenceRecordFailRepository;
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
                .fieldSetMapper(fieldSet ->
                    GeoreferenceRecord.builder()
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
                            .build()
                )
                .build();
    }

    @Bean
    public ItemWriter<GeoreferenceRecord> writer() {
        return chunk -> {
            if (georeferenceRecordProcessor.getErrorList().isEmpty()) {
                for (GeoreferenceRecord item : chunk.getItems()) {
                    GeoreferenceRecord georeferenceRecord = getGeoreferenceRecord(item);
                    georeferenceRecordRepository.save(georeferenceRecord);
                }
            } else {
                georeferenceRecordFailRepository.saveAll(georeferenceRecordProcessor.getErrorList());
            }
        };
    }

    private static GeoreferenceRecord getGeoreferenceRecord(GeoreferenceRecord item) {
        GeoreferenceRecord georeferenceRecord = new GeoreferenceRecord();
        georeferenceRecord.setGeoreferenceRequest(item.getGeoreferenceRequest());
        georeferenceRecord.setFarmerName(item.getFarmerName());
        georeferenceRecord.setDocumentType(item.getDocumentType());
        georeferenceRecord.setDocumentNumber(item.getDocumentNumber());
        georeferenceRecord.setFarmName(item.getFarmName());
        georeferenceRecord.setCultivationArea(item.getCultivationArea());
        georeferenceRecord.setMunicipalityCode(item.getMunicipalityCode());
        georeferenceRecord.setMunicipalityName(item.getMunicipalityName());
        georeferenceRecord.setDepartmentCode(item.getDepartmentCode());
        georeferenceRecord.setDepartmentName(item.getDepartmentName());
        georeferenceRecord.setOldPlot(false);
        georeferenceRecord.setStatus("");
        georeferenceRecord.setGeoJsonId("");
        return georeferenceRecord;
    }

    @Bean(name = "csvImporterJob")
    public Job csvImporterJob(JobRepository jobRepository,
                              GeoreferenceRequestService georeferenceRequestService,
                              FileService fileService,
                              SicaProducer sicaProducer,
                              GeoreferenceRecordRepository georeferenceRecordRepository
    ) throws Exception {
        return new JobBuilder("csvImporterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                //.start(masterStep())
                .listener(new BatchJobExecuteListener(georeferenceRequestService, fileService, sicaProducer, georeferenceRecordRepository))
                //.validator(parameters -> System.out.println(parameters.getLong("requestId")))
                .flow(csvImporterStep())
                .end()
                .build();
    }

    @Bean(name = "csvImporterStep")
    public Step csvImporterStep() throws Exception {
        return new StepBuilder("csvImporterStep", jobRepository())
                .<GeoreferenceRecord, GeoreferenceRecord>chunk(100, platformTransactionManager())
                .reader(reader())
                .processor(georeferenceRecordProcessor)
                .writer(writer())
                .listener(new BatchStepExecutionListener(reader()))
                //.taskExecutor(taskExecutor())
                .faultTolerant()
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(platformTransactionManager());
        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
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
