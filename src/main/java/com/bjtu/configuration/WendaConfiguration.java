package com.bjtu.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.bjtu.interceptor.LoginRequiredInterceptor;
import com.bjtu.interceptor.PassportInterceptor;

@Component
public class WendaConfiguration extends WebMvcConfigurerAdapter {
	@Autowired
	PassportInterceptor passportInterceptor;
	
	@Autowired
	LoginRequiredInterceptor loginRequiredInterceptor;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		registry.addInterceptor(passportInterceptor);
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*")
		.addPathPatterns("/msg/*"); 
		super.addInterceptors(registry);
	}
	
}
