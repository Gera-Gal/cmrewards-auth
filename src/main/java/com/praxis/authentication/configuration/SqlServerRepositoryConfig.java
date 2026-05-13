package com.praxis.authentication.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.praxis.authentication.data.repository.sqlserver",
    entityManagerFactoryRef = "sqlServerEntityManagerFactory",
    transactionManagerRef = "sqlServerTransactionManager"
)
public class SqlServerRepositoryConfig {
    // Configuración específica para repositorios SQL Server
}
