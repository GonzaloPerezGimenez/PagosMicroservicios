package com.Proyect.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UserServiceApp
 * Clase principal de la aplicación Spring Boot.
 * Punto de entrada que inicia el servidor de aplicación.
 */
@SpringBootApplication
public class UserServiceApp {

	/**
	 * Método main - Punto de entrada de la aplicación.
	 * Inicia el servidor embebido (Tomcat) y carga la configuración Spring.
	 *
	 * @param args Argumentos de línea de comandos (opcional)
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApp.class, args);
	}

}
