package school.faang.user_service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
class SpringContextStartsTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ApplicationContext context;

    @ParameterizedTest
    @ValueSource(classes = {
            org.springframework.web.bind.annotation.RestController.class,
            org.springframework.stereotype.Service.class,
            org.springframework.stereotype.Component.class,

    })
    void testBeanInjectionForAnnotations(Class<?> annotationClass) {
        Class<? extends Annotation> annotationType = annotationClass.asSubclass(Annotation.class);
        String[] beanNames = context.getBeanNamesForAnnotation(annotationType);

        assertThat(beanNames).isNotEmpty();
        Arrays.stream(beanNames)
                .map(context::getBean)
                .forEach(bean -> assertNotNull(bean, "Bean should not be null: " + bean));
    }
}
