package com.georeference;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@OpenAPIDefinition(info = @Info(
		title = "Georeference Process File API",
		version = "1.0",
		description = "Description"
))
public class ProcessFileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessFileServiceApplication.class, args);
	}

}
