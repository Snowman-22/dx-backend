package com.snowman.team2;

import com.snowman.team2.global.storage.S3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(S3Properties.class)
public class Team2Application {

	public static void main(String[] args) {
		SpringApplication.run(Team2Application.class, args);
	}

}
