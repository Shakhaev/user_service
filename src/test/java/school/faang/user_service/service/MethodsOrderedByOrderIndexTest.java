package school.faang.user_service.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodsOrderedByOrderIndexTest {

    @Test
    @Order(1)
    public void testC() {
        System.out.println("Running test C");
    }

    @Test
    @Order(3)
    public void testB() {
        System.out.println("Running test B");
    }

    @Test
    @Order(4)
    public void testA() {
        System.out.println("Running test A");
    }

    @Test
    @Order(2)
    public void testD() {
        System.out.println("Running test D");
    }

}
