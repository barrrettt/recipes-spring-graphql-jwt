package com.example.recipes;

import com.example.recipes.data.ToolsJPA;
import com.example.recipes.data.repos.UserRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//spring app main class
@SpringBootApplication
public class RecipesApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RecipesApplication.class, args);
	}

	//log 
	private static final Logger log = LoggerFactory.getLogger(RecipesApplication.class); 

	@Autowired 
	private SecurityDataFetcher security; 

	//entrypoint 
	@Bean 
	public CommandLineRunner initApp(ToolsJPA toolsJPA,UserRepo userR) { 
		return (args) -> { 
			log.info("********** INIT DATABASE ***********"); 
			//if database is empty: 
			Long count = userR.count(); 
			if (count == 0) { 
				log.info("MINIMAL DATA: 3 ROLES 1 ADMIN (admin,admin) ..."); 
				toolsJPA.initDataBase(); 
				//dummy data 
				log.info("POBLATE DB WITH MOBS..."); 
				toolsJPA.createMobs(); //comment me 
				log.info("DATABASE READY"); 
			} else{
				security.init(); 
			}
			log.info("********** API READY ***********"); 
		};
	}
}
