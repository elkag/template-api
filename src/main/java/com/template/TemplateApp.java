package com.template;

import com.template.config.application.properties.AppProperties;
import com.template.config.lucene.HibernateSearchConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
@EntityScan({"com.template"})
@Import(HibernateSearchConfig.class)
public class TemplateApp {
    public static void main(String[] args) {
            SpringApplication.run(TemplateApp.class, args);
        }
}
