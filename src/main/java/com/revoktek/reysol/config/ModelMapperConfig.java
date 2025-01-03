package com.revoktek.reysol.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
//        return new ModelMapper();
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setPropertyCondition(context -> {
            Object source = context.getSource();
            return !(source instanceof org.hibernate.proxy.HibernateProxy ||
                    source instanceof jakarta.persistence.Entity);
        });

        return modelMapper;
    }

}
