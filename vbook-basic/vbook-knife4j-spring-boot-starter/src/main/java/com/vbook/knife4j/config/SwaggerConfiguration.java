package com.vbook.knife4j.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Description:
 * @Auther: zhouhuan
 * @Date: 2022/8/1-17:39
 */
@Configuration
@EnableOpenApi
public class SwaggerConfiguration {
 @Bean
 public Docket buildDocket() {
  return new Docket(DocumentationType.SWAGGER_2)
          .apiInfo(buildApiInfo())
          .select()
          // 要扫描的API(Controller)基础包
          .apis(RequestHandlerSelectors.basePackage("com.vbook"))
          .paths(PathSelectors.any())
          .build();
 }
 private ApiInfo buildApiInfo() {
  Contact contact = new Contact("zhouhuan","","");
  return new ApiInfoBuilder()
          .title("微读书API开发文档")
          .description("微读书服务api")
          .contact(contact)
          .version("1.0.0").build();
 }
}
