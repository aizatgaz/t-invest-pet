package org.invest.configurations;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class PropertiesConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        var yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("tinvest.yaml"));
        var configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(yaml.getObject());
        return configurer;
    }

}
