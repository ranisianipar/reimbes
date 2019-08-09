package com.reimbes;

import com.reimbes.implementation.AdminServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = AdminServiceImpl.class)
public class AdminServiceTest {
    
}
