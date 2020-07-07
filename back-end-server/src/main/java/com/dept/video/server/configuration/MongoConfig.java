package com.dept.video.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

    private final MongoDbFactory mongoDbFactory;
    private final MongoMappingContext mongoMappingContext;

    @Autowired
    public MongoConfig(MongoDbFactory mongoDbFactory, MongoMappingContext mongoMappingContext) {
        this.mongoDbFactory = mongoDbFactory;
        this.mongoMappingContext = mongoMappingContext;
    }

    @Bean
    public MappingMongoConverter getMappingMongoConverter() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mappingMongoConverter.setMapKeyDotReplacement("_");
        mappingMongoConverter.afterPropertiesSet();
        return mappingMongoConverter;
    }
}
