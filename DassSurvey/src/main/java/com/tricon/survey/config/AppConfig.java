package com.tricon.survey.config;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "jpaEntityManagerFactory", transactionManagerRef = "jpaTransactionManager", basePackages = {
		"com.tricon.survey.jpa.repository" })
public class AppConfig {

	
	@Autowired
	private Environment env;
	
	@Bean(name = "jpaDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "jpaEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("jpaDataSource") DataSource dataSource) {
		
		 Map<String, Object> properties = new HashMap<>();
		    properties.put("hibernate.hbm2ddl.auto",
		            env.getProperty("spring.jpa.hibernate.ddl-auto"));
		    
		    properties.put("hibernate.dialect",
		            env.getProperty("spring.jpa.database-platform"));
		    properties.put("hibernate.show_sql",env.getProperty("spring.jpa.show-sql"));
		    return builder
		            .dataSource(dataSource)
		            .properties(properties)
		            .packages("com.tricon.survey.db.entity")
		            .persistenceUnit("spring")
		            .build();
	}
	
	@Bean(name = "jpaTransactionManager")
	public PlatformTransactionManager jpaTransactionManager(
			
			@Qualifier("jpaEntityManagerFactory") EntityManagerFactory jpaEntityManagerFactory)
	{
		return new JpaTransactionManager(jpaEntityManagerFactory);
	}
	
}
