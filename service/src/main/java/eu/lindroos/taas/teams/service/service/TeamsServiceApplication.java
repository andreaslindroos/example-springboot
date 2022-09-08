package eu.lindroos.taas.teams.service.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TeamsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamsServiceApplication.class, args);
    }

}
