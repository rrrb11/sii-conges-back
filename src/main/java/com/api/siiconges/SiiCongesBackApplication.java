package com.api.siiconges;

import java.net.URI;
import java.awt.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SiiCongesBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiiCongesBackApplication.class, args);
		openSwagger();
	}

	private static void openSwagger() {
		System.setProperty("java.awt.headless", "false");
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI("http://localhost:9000/swagger-ui/index.html"));
		}
		catch(Exception e) {
			System.out.println("ERROR LOADING PAGE");
		}
	}

}
