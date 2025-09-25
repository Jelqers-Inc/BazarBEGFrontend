package org.esfr.BazarBEG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class BazarBegApplication {

	public static void main(String[] args) {
		SpringApplication.run(BazarBegApplication.class, args);
	}
}