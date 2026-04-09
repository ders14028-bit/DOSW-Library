package edu.eci.dosw.tdd.persistence.relational;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("relational")
@EnableJpaRepositories(basePackages = "edu.eci.dosw.tdd.persistence.relational.repository")
public class RelationalConfig {
}