package com.example.weather.web;

import com.example.weather.WeatherAppProperties;
import com.example.weather.integration.ows.Weather;
import com.example.weather.integration.ows.WeatherService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/")
public class WeatherSummaryController {

    private final WeatherService weatherService;

    private final WeatherAppProperties properties;

    public WeatherSummaryController(WeatherService weatherService,
                                    WeatherAppProperties properties) {
        this.weatherService = weatherService;
        this.properties = properties;
    }

    @RequestMapping(value = "weather.stream", produces = "text/event-stream")
    public Flux<WeatherSummary> weatherStream() throws Exception {
       return Flux.interval(Duration.ZERO, Duration.ofSeconds(5))
                .flatMap(i -> this.getLocations())
                .flatMap(l -> Flux.zip(Mono.just(l), weatherService.getWeather(l.getCountry(), l.getCity())))
                .map(t2 -> createWeatherSummary(t2.getT1(), t2.getT2()));
    }

    private Flux<Location> getLocations() {
        return Flux.fromStream(
                this.properties.getLocations().stream()
                        .map(l -> l.split("/"))
                        .map(args -> new Location(args[0], args[1]))
        );
    }

    private WeatherSummary createWeatherSummary(Location location,
                                                Weather weather) {
        // cough cough
        if ("Las Vegas".equals(location.getCity())) {
            weather.setWeatherId(666);
        }
        return new WeatherSummary(location.getCountry(), location.getCity(), weather);
    }

}
