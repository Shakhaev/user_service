package school.faang.user_service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
class SpringContextStartsTest {

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
                .forEach(bean -> assertNotNull(
                        bean, "Bean annotated with " + annotationClass.getName() + " should not be null: " + bean));
    }
}
