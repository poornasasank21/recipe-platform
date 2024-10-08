package com.user.userservice.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = UserServiceConfiguration.class)
class UserServiceConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void modelMapperBeanShouldBeCreated() {
        assertTrue(context.containsBean("modelMapper"), "ModelMapper bean should exist in the application context.");
        ModelMapper modelMapper = context.getBean(ModelMapper.class);
        assertNotNull(modelMapper, "ModelMapper bean should not be null");
    }
}