package ar.edu.iua.iw3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Iw3Application extends SpringBootServletInitializer implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(Iw3Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}

	

}
