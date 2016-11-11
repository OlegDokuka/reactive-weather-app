package com.example.weather.integration.ows;

import com.example.weather.WeatherAppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.reactive.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.client.reactive.ClientRequest.GET;

@Service
public class WeatherService {

    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/weather?q={city},{country}&APPID={key}";

    private static final String FORECAST_URL =
            "http://api.openweathermap.org/data/2.5/forecast?q={city},{country}&APPID={key}";

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WebClient webClient;

    private final String apiKey;

    public WeatherService(WeatherAppProperties properties) {
        this.webClient = WebClient.create(new ReactorClientHttpConnector());
        this.apiKey = properties.getApi().getKey();
    }

    @Cacheable("weather")
    public Mono<Weather> getWeather(String country, String city) {
        logger.info("Requesting current weather for {}/{}", country, city);
        URI url = new UriTemplate(WEATHER_URL).expand(city, country, this.apiKey);
        return invoke(url, Weather.class);
    }

    @Cacheable("forecast")
    public Mono<WeatherForecast> getWeatherForecast(String country, String city) {
        logger.info("Requesting weather forecast for {}/{}", country, city);
        URI url = new UriTemplate(FORECAST_URL).expand(city, country, this.apiKey);
        return invoke(url, WeatherForecast.class);
    }

    private <T> Mono<T> invoke(URI url, Class<T> responseType) {
        return webClient
                .exchange(GET(url.toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .build())
                .then(response -> response.bodyToMono(responseType));
    }

}
