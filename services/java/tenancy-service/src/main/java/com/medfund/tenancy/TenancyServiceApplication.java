package com.medfund.tenancy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.medfund.tenancy", "com.medfund.shared"})
public class TenancyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenancyServiceApplication.class, args);
    }
}
