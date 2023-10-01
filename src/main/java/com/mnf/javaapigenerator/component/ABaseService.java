package com.mnf.javaapigenerator.component;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ABaseService<T> {
    @Autowired
    EntityManager entityManager;
    private CustomQueryBuilder<T> customQueryBuilder;

    public static final Logger logger = LoggerFactory.getLogger(ABaseService.class);

    public CustomQueryBuilder<T> getQueryBuilder(){
        this.customQueryBuilder = new CustomQueryBuilder<>(entityManager);

        return this.customQueryBuilder;
    }
}
