package ec.edu.espe.me_eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MsEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsEurekaServerApplication.class, args);
    }

}