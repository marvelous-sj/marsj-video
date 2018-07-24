package xyz.marsj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages="xyz.marsj.mapper")
@ComponentScan(basePackages= {"xyz.marsj","org.n3r.idworker"})
public class Application extends SpringBootServletInitializer{
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	  @Override//为了打包spring boot项目
	  protected SpringApplicationBuilder configure(
	            SpringApplicationBuilder builder) {
	        return builder.sources(this.getClass());
	   }
}
