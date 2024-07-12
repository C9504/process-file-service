package com.georeference.config.batch;

import com.georeference.entities.GeoreferenceRecord;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${csv.file.path}")
    private Resource csvFilePath;

    public static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    private static  final String[] FIELDS = new String[] {
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
                .resource(new ClassPathResource("converted.csv"))
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
    public JpaItemWriter<GeoreferenceRecord> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<GeoreferenceRecord>()
                //.usePersist(true)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step csvImporterStep(ItemReader<GeoreferenceRecord> reader, ItemWriter<GeoreferenceRecord> writer, JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, GeoreferenceRecordProcessor georeferenceRecordProcessor) {
        return new StepBuilder("csvImporterStep", jobRepository)
                .<GeoreferenceRecord, GeoreferenceRecord>chunk(5, platformTransactionManager)
                .reader(reader)
                .writer(writer)
                .processor(georeferenceRecordProcessor)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job csvImporterJob(Step csvImporterStep, JobRepository jobRepository) {
        return new JobBuilder("csvImporterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .validator(parameters -> System.out.println(parameters.getLong("requestId")))
                .flow(csvImporterStep)
                .end()
                .build();
    }

    @Bean
    public DefaultLineMapper<GeoreferenceRecord> lineMapper(LineTokenizer tokenizer) {
        var lineMapper = new DefaultLineMapper<GeoreferenceRecord>();
        lineMapper.setLineTokenizer(tokenizer);
        //lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public DelimitedLineTokenizer tokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames(FIELDS);
        return tokenizer;
    }
}
