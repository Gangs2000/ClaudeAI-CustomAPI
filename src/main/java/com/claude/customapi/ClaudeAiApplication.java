package com.claude.customapi;

import com.claude.customapi.Service.CityInfoService;
import com.claude.customapi.Service.WeatherInfoService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClaudeAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClaudeAiApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider toolCallbackProvider(WeatherInfoService weatherInfoService, CityInfoService cityInfoService) {
		return MethodToolCallbackProvider.builder().toolObjects(weatherInfoService, cityInfoService).build();
	}
}
