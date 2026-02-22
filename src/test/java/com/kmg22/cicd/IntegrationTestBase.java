package com.kmg22.cicd;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;

@SuppressWarnings("resource")
public abstract class IntegrationTestBase {

    @Container
    static MariaDBContainer<?> mariadb =
            new MariaDBContainer<>("mariadb:11")
                    .withDatabaseName("cicd_test")
                    .withUsername("cicd_user")
                    .withPassword("cicd_pw");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
}
