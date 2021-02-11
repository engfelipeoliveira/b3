package br.com.selenium;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.selenium.service.MainService;

@SpringBootApplication
public class B3Application implements CommandLineRunner{

	private final MainService mainService;
	
	public B3Application(MainService mainService) {
		this.mainService = mainService;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(B3Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		this.mainService.execute();
	}

}
