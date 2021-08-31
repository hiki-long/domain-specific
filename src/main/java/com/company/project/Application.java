package com.company.project;

import com.company.project.configurer.WebMvcConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ServletComponentScan
public class Application extends WebMvcConfigurer {
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 请注意下面这个映射，将资源路径 /ts 下的资源，映射到根目录为 /ts的访问路径下
        // 如 ts下的ts.html, 对应的访问路径 /ts/ts
        registry.addResourceHandler("/**").addResourceLocations("file:" + System.getProperty("user.dir") + "/picture/avatar/");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

