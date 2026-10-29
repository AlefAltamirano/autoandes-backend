package pe.edu.upeu.autoandes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "pe.edu.upeu.autoandes")
public class AutoandesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoandesBackendApplication.class, args);
	}

}
