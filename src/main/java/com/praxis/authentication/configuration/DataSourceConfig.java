package com.praxis.authentication.configuration;

import com.praxis.authentication.util.AESUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    // =========================
    // SQL SERVER (externo)
    // =========================
    @Value("${spring.datasource.sqlserver.url}")
    private String sqlUrl;

    @Value("${spring.datasource.sqlserver.username}")
    private String sqlUser;

    @Value("${spring.datasource.sqlserver.password}")
    private String sqlEncryptedPassword;

    @Value("${spring.datasource.sqlserver.driver-class-name}")
    private String sqlDriver;

    // =========================
    // MYSQL (interno)
    // =========================
    @Value("${spring.datasource.mysql.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.mysql.username}")
    private String mysqlUser;

    @Value("${spring.datasource.mysql.password}")
    private String mysqlEncryptedPassword;
    
    @Value("${spring.datasource.mysql.driver-class-name}")
    private String mysqlDriver;

    @Bean(name = "sqlServerDataSource")
    @Primary
    public DataSource sqlServerDataSource() throws Exception {
        log.info("🔌 Inicializando DataSource para SQL Server");
        log.info("   URL: {}", sqlUrl);
        log.info("   Usuario: {}", sqlUser);
        
        long startTime = System.currentTimeMillis();
        String password = AESUtil.decrypt(sqlEncryptedPassword);
        long endTime = System.currentTimeMillis();
        log.info("⏱️ Desencriptación de contraseña SQL Server completada en {} ms", (endTime - startTime));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(sqlUrl);
        config.setUsername(sqlUser);
        config.setPassword(password);
        config.setDriverClassName(sqlDriver);
        config.setPoolName("SQLServerPool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        log.info("✅ Configuración HikariCP para SQL Server completada");
        
        HikariDataSource dataSource = new HikariDataSource(config);
        log.info("✅ SQLServerPool inicializado correctamente");
        
        return dataSource;
    }

    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource() throws Exception {
        log.info("🔌 Inicializando DataSource para MySQL");
        log.info("   URL: {}", mysqlUrl);
        log.info("   Usuario: {}", mysqlUser);
        
        long startTime = System.currentTimeMillis();
        String password = AESUtil.decrypt(mysqlEncryptedPassword);
        long endTime = System.currentTimeMillis();
        log.info("⏱️ Desencriptación de contraseña MySQL completada en {} ms", (endTime - startTime));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mysqlUrl);
        config.setUsername(mysqlUser);
        config.setPassword(password);
        config.setDriverClassName(mysqlDriver);
        config.setPoolName("MySQLPool");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        log.info("✅ Configuración HikariCP para MySQL completada");
        
        HikariDataSource dataSource = new HikariDataSource(config);
        log.info("✅ MySQLPool inicializado correctamente");
        
        return dataSource;
    }
}