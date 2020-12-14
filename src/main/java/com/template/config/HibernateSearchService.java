package com.template.config;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Service
public class HibernateSearchService {
    private final FullTextEntityManager fullTextEntityManager;

    public HibernateSearchService(EntityManagerFactory entityManagerFactory){
        fullTextEntityManager = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
    }

    public void triggerIndexing() {
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
