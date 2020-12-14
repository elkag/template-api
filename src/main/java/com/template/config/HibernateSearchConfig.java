package com.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class HibernateSearchConfig {

    @Bean
    public HibernateSearchService luceneIndexServiceBean(EntityManagerFactory entityManagerFactory){
        HibernateSearchService hibernateSearchService = new HibernateSearchService(entityManagerFactory);
        hibernateSearchService.triggerIndexing();
        return hibernateSearchService;
    }
}
