package xyz.marsj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import xyz.marsj.interceptor.MyInterceptor;
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("file:/home/marsj/image/video/")
		.addResourceLocations("classpath:/META-INF/resources/");
	}
	
	@Bean
	public MyInterceptor myInterceptor() {
		return new MyInterceptor();
	}
	
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(myInterceptor()).addPathPatterns("/user/**")
	       .addPathPatterns("/video/upload", "/video/uploadCover",
	    		   "/video/userlike", "/video/userunlike","/video/savecomment")
	       .addPathPatterns("/bgm/list").excludePathPatterns("/user/querypublisher");
		super.addInterceptors(registry);
	}
}
