package dev.dividendproject;

import dev.dividendproject.model.Company;
import dev.dividendproject.scraper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DividendProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DividendProjectApplication.class, args);
//		System.out.println("Main -> " + Thread.currentThread().getName());

	}
}

