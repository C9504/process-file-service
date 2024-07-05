package com.georeference.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing //(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class BatchConfig {

}
