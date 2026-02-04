package io.reloop.dpp;

import org.springframework.boot.SpringApplication;

public class TestReloopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(ReloopBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
