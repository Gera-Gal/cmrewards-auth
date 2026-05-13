package com.praxis.authentication.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    private static final Logger log = LoggerFactory.getLogger(JpaConfig.class);

    // ==================== CONFIGURACIÓN PARA MYSQL ====================
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        
        log.info("📦 Creando EntityManagerFactory para MySQL");
        log.info("   Paquete de entidades: com.praxis.authentication.data.entity.mysql");
        
        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(dataSource)
                .packages("com.praxis.authentication.data.entity.mysql")
                .persistenceUnit("mysql")
                .build();
        
        log.info("✅ EntityManagerFactory para MySQL creado exitosamente");
        
        return emf;
    }

    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory) {
        log.info("🔧 Creando TransactionManager para MySQL");
        return new JpaTransactionManager(mysqlEntityManagerFactory.getObject());
    }

    // ==================== CONFIGURACIÓN PARA SQL SERVER ====================
    @Bean(name = "sqlServerEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean sqlServerEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sqlServerDataSource") DataSource dataSource) {
        
        log.info("📦 Creando EntityManagerFactory para SQL Server (PRIMARY)");
        log.info("   Paquete de entidades: com.praxis.authentication.data.entity.sqlserver");
        
        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(dataSource)
                .packages("com.praxis.authentication.data.entity.sqlserver")
                .persistenceUnit("sqlserver")
                .build();
        
        log.info("✅ EntityManagerFactory para SQL Server creado exitosamente");
        
        return emf;
    }

    @Bean(name = "sqlServerTransactionManager")
    @Primary
    public PlatformTransactionManager sqlServerTransactionManager(
            @Qualifier("sqlServerEntityManagerFactory") LocalContainerEntityManagerFactoryBean sqlServerEntityManagerFactory) {
        log.info("🔧 Creando TransactionManager para SQL Server (PRIMARY)");
        return new JpaTransactionManager(sqlServerEntityManagerFactory.getObject());
    }
}