package de.uftos.timefoldQuickstart;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:postgresql://db:5432/uftos");
        dataSourceBuilder.username("uftos");
        dataSourceBuilder.password("superSecurePassword");
        return dataSourceBuilder.build();
    }
}
