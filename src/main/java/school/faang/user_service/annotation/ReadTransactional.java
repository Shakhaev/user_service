package school.faang.user_service.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional(rollbackFor = Throwable.class, readOnly = true)
@Documented
public @interface ReadTransactional {
}
