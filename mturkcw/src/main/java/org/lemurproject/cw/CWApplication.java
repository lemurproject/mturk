package org.lemurproject.cw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.lemurproject.cw")
public class CWApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CWApplication.class, args);
    }


}
