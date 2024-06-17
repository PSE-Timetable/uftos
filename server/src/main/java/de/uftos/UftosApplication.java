package de.uftos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UftosApplication {

	public static void main(String[] args) {
		SpringApplication.run(UftosApplication.class, args);
		databaseSetup();
	}

	private static void databaseSetup() {
		//TODO: database setup
		System.out.println("TODO: actual database setup \n (not yet doing anything)");
	}
}
