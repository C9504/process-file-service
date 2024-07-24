package com.georeference.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
//@PropertySource({"classpath:application.yaml"})
@EnableJpaRepositories(
        entityManagerFactoryRef = "appRegCaEntityManagerFactory",
        transactionManagerRef = "appRegCaTransactionManager",
        basePackages = "com.georeference.appregca.repositories"
)
public class AppRegCaConfig {


    @Bean(name = "appRegCaDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.appregca")
    public DataSourceProperties appRegCaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "appregCaDataSource")
    //@ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource appregCaDataSource(@Qualifier("appRegCaDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "appRegCaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean appRegCaEntityManagerFactory
            (@Qualifier("appregCaDataSource") DataSource dataSource,
             EntityManagerFactoryBuilder builder
             //@Qualifier("cacheManager") CacheManager cacheManager
            ) {
        //Map<String, Object> props = new HashMap<>();
        //props.put("hibernate.javax.cache.CacheManager", cacheManager);
        return builder.dataSource(dataSource)
                .packages("com.georeference.appregca.entities")
                //.properties(props)
                .persistenceUnit("appRegCa").build();
    }

    @Bean(name = "appRegCaTransactionManager")
    public PlatformTransactionManager appRegCaTransactionManager(@Qualifier("appRegCaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

}
