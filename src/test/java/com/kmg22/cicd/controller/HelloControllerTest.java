package com.kmg22.cicd.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloControllerTest {

    @Test
    void hello_ShouldReturnMessage() {
        HelloController controller = new HelloController();
        assertEquals("Hello, CI/CD Pipeline!", controller.hello());
    }

    @Test
    void health_ShouldReturnOK() {
        HelloController controller = new HelloController();
        assertEquals("OK", controller.health());
    }
}