package com.startinpoint.proj.familyapp.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 
 * @author nankhinmhwe
 *
 */
public class Initializer implements WebApplicationInitializer{

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebAppConfig.class);
		container.addListener(new ContextLoaderListener(context));
		container.addListener(FamilyAppDeployListener.class);
		context.setServletContext(container);
		
		// set default encoding to utf-8 for all request
		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		FilterRegistration.Dynamic characterEncoding = container.addFilter("characterEncoding",
						characterEncodingFilter);
		characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
		
		ServletRegistration.Dynamic registration = container.addServlet("dispatcher", new DispatcherServlet(context));
		registration.setLoadOnStartup(1);
		registration.addMapping("/");

	}

}
