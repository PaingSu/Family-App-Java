package com.startinpoint.proj.familyapp.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startinpoint.proj.familyapp.webservice.entity.FamilyAppConst;

/**
 * 
 * @author nankhinmhwe
 *
 */
@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages={"com.startinpoint.proj.familyapp"})
@PropertySource("classpath:log4j.properties")
public class WebAppConfig extends WebMvcConfigurerAdapter{
	
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(10485760); // 10MB
		multipartResolver.setMaxUploadSizePerFile(1048576); // 1MB
		return multipartResolver;
	}
	
	@Bean
	public InternalResourceViewResolver resolver(){
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	
	/**
	 * Configure Cross Origin Request Processing
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedOrigins("*")
		.allowedMethods("GET","HEAD","OPTIONS","POST","PUT")
		.allowedHeaders("Content-Type","Access-Control-Allow-Origin","Access-Control-Allow-Headers", "Origin","Headers","Authorization")
		.allowCredentials(false).maxAge(3600);
	}
	
	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();

		DateFormat df = new SimpleDateFormat(FamilyAppConst.CALENDAR_DATETIME_FORMAT);
		df.setTimeZone(TimeZone.getDefault());
		mapper.setDateFormat(df);
		return mapper;
	}

}
