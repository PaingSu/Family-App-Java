package com.startinpoint.proj.familyapp.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hibernate Configuration
 * @author nankhinmhwe
 *
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class HibernateConfig {
	
	@Resource
	private Environment environment;
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(getDataSource());
		sessionFactory.setPackagesToScan(new String[] { "com.startinpoint.proj.familyapp" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}


	@Bean
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName(environment.getProperty("database.driver"));
	    dataSource.setUrl(environment.getProperty("database.url"));
	    dataSource.setUsername(environment.getProperty("database.username"));
	    dataSource.setPassword(environment.getProperty("database.password"));
	    return dataSource;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
        return properties; 
	}
	
	 @Bean
	 @Autowired
	 public HibernateTransactionManager transactionManager(SessionFactory s) {
	      HibernateTransactionManager txManager = new HibernateTransactionManager();
	      txManager.setSessionFactory(s);
	      return txManager;
	 }
}
