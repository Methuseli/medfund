package com.medfund.contributions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.medfund.contributions", "com.medfund.shared"})
public class ContributionsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContributionsServiceApplication.class, args);
    }
}
