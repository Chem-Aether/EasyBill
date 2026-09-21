package com.travel.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TravelDatabaseInitializer implements InitializingBean {
    private final DynamicRoutingDataSource routingDataSource;

    public TravelDatabaseInitializer(DataSource dataSource) {
        if (!(dataSource instanceof DynamicRoutingDataSource dynamicDataSource)) {
            throw new IllegalStateException("动态数据源未正确初始化");
        }
        this.routingDataSource = dynamicDataSource;
    }

    @Override
    public void afterPropertiesSet() {
        DataSource travelDataSource = routingDataSource.getDataSource("travel");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("db/travel-schema.sql"));
        populator.setContinueOnError(false);
        populator.execute(travelDataSource);
    }
}
