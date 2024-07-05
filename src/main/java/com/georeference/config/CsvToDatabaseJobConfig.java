package com.georeference.config;

import com.georeference.config.batch.listeners.LandmarkItemProcessListener;
import com.georeference.entities.CsvUpload;
import com.georeference.entities.DetailedValidationError;
import com.georeference.entities.Landmark;
import com.georeference.repositories.CsvUploadRepository;
import com.georeference.repositories.DetailedValidationErrorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class CsvToDatabaseJobConfig {

    @Value("${csv.file.path}")
    private Resource csvFilePath;

    public static final Logger logger = LoggerFactory.getLogger(CsvToDatabaseJobConfig.class);

    private static final String INSERT_QUERY = """
      insert into landmarks (name, latitude, longitude, address, description, upload_id)
      values (:name,:latitude,:longitude,:address,:description,:uploadId)""";

    private final JobRepository jobRepository;

    @Autowired
    private DetailedValidationErrorRepository detailedValidationErrorRepository;

    @Autowired
    private CsvUploadRepository csvUploadRepository;

    private final AtomicLong recordIdGenerator = new AtomicLong();
    private CsvUpload currentUpload;

    public CsvToDatabaseJobConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
        this.currentUpload = new CsvUpload();
    }

    @Bean(name = "insertIntoDbFromCsvJob")
    public Job insertIntoDbFromCsvJob(Step step1, Step step2) {
        var name = "Landmarks Import Job";
        var builder = new JobBuilder(name, jobRepository);
        return builder.start(step1)
                .build();
    }

    @Bean
    public Step step1(ItemReader<Landmark> reader,
                      ItemWriter<Landmark> writer,
                      ItemProcessor<Landmark, Landmark> processor,
                      PlatformTransactionManager txManager) {

        var name = "INSERT CSV RECORDS To DB Step";
        var builder = new StepBuilder(name, jobRepository);
        return builder
                .<Landmark, Landmark>chunk(5, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new LandmarkItemProcessListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<Landmark> reader(
            LineMapper<Landmark> lineMapper) {
        var itemReader = new FlatFileItemReader<Landmark>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);
        itemReader.setStrict(false);
        itemReader.setResource(csvFilePath);
        return itemReader;
    }

    @Bean
    public DefaultLineMapper<Landmark> lineMapper(LineTokenizer tokenizer,
                                                  FieldSetMapper<Landmark> fieldSetMapper) {
        var lineMapper = new DefaultLineMapper<Landmark>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public BeanWrapperFieldSetMapper<Landmark> fieldSetMapper() {
        var fieldSetMapper = new BeanWrapperFieldSetMapper<Landmark>();
        fieldSetMapper.setTargetType(Landmark.class);
        return fieldSetMapper;
    }

    @Bean
    public DelimitedLineTokenizer tokenizer() {
        var tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("name", "latitude", "longitude", "address", "description");
        return tokenizer;
    }

    @Bean
    public JdbcBatchItemWriter<Landmark> jdbcItemWriter(DataSource dataSource) {
        CsvUpload csvUpload = new CsvUpload();
        csvUpload.setUploadTimestamp(LocalDateTime.now());
        currentUpload = csvUploadRepository.save(csvUpload);
        var provider = new BeanPropertyItemSqlParameterSourceProvider<Landmark>();
        var itemWriter = new JdbcBatchItemWriter<Landmark>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(INSERT_QUERY);
        itemWriter.setItemSqlParameterSourceProvider(provider);
        return itemWriter;
    }

    @Bean
    public ItemProcessor<Landmark, Landmark> processor() {
        return landmark -> {
            Long recordId = recordIdGenerator.incrementAndGet();
            List<DetailedValidationError> errors = new ArrayList<>();

            if (landmark.getName() == null || landmark.getName().isEmpty()) {
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "name", "Name is required"));
            } else {
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "name", "Correcto"));
            }
            if (!isValidCoordinates(landmark.getLatitude(), landmark.getLongitude())) {
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "latitude", "Invalid latitude"));
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "longitude", "Invalid longitude"));
            } else {
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "latitude", "Correcto"));
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "longitude", "Correcto"));
            }
            if (landmark.getAddress() == null || landmark.getAddress().isEmpty()) {
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "address", "Address is required"));
            } else {
                errors.add(new DetailedValidationError(currentUpload.getId(), recordId, "address", "Correcto"));
            }

            detailedValidationErrorRepository.saveAll(errors);

            if (errors.isEmpty()) {
                landmark.setUploadId(currentUpload.getId());
                return landmark;
            } else {
                return null;
            }
        };
    }

    private boolean isValidCoordinates(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

}
