package edu.eci.dosw.tdd.persistence.nonrelational;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@Profile("mongo")
@EnableMongoRepositories(basePackages = "edu.eci.dosw.tdd.persistence.nonrelational.repository")
public class MongoConfig {
}

