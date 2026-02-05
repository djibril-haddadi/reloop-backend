package io.reloop.dpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EnableJpaAuditing
public class ReloopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReloopBackendApplication.class, args);
	}

	// C'est lui qui va parler à la base de données au démarrage
	@Bean
	public ApplicationRunner enablePostgis(JdbcTemplate jdbcTemplate) {
		return args -> {
			// On exécute la commande SQL magique automatiquement
			jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS postgis");
			System.out.println("🌍 PostGIS extension activated successfully!");
		};
	}
}
