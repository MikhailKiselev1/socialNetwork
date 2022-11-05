package org.javaproteam27.socialnetwork.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.config.ParserConfig;
import org.javaproteam27.socialnetwork.model.dto.request.WeatherRq;
import org.javaproteam27.socialnetwork.model.dto.response.WeatherRs;
import org.javaproteam27.socialnetwork.model.entity.City;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CityRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private final PersonRepository personRepository;
    private final CityRepository cityRepository;
    private final ParserConfig parserConfig;
    private static final String WEATHER_TOKEN = "b392316d11d749cca0251337222510";

    public WeatherRs getWeather(String city) {
        var cityEntity = cityRepository.findByTitle(city);
        if (!cityEntity.isEmpty()) {
            return WeatherRs.builder()
                    .city(cityEntity.get(0).getTitle())
                    .clouds(cityEntity.get(0).getClouds())
                    .temp(cityEntity.get(0).getTemp())
                    .build();
        } else {
            var cityMoscow = cityRepository.findByTitle("Москва");
            if (cityMoscow.isEmpty()) {
                return WeatherRs.builder().build();
            } else return WeatherRs.builder()
                    .city(cityMoscow.get(0).getTitle())
                    .clouds(cityMoscow.get(0).getClouds())
                    .temp(cityMoscow.get(0).getTemp())
                    .build();
        }
    }

    private WeatherRq getWeatherFromSite(String city) {
        String url = "http://api.weatherapi.com/v1/current.json?key="+ WEATHER_TOKEN + "&q=" + city + "&aqi=no&lang=ru";
        WeatherRq rq = new WeatherRq();
        try {
            var weatherDoc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .execute()
                    .body();
            JSONObject weather = (JSONObject) new JSONParser().parse(weatherDoc);
            JSONObject currentWeather = (JSONObject) weather.get("current");
            JSONObject clouds = (JSONObject) currentWeather.get("condition");
            rq.setTemp(currentWeather.get("temp_c").toString());
            rq.setClouds(clouds.get("text").toString());
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rq;
    }

    @Scheduled(initialDelay = 5000, fixedRateString = "PT2H")
    @Async
    public void refreshWeather() {
        if (parserConfig.isEnabled()) {
            log.info("Requesting weather started");
            var cityList = getAllCity();
            for (String city : cityList) {
                WeatherRq weatherRq;
                if (Objects.equals(city, "Санкт-Петербург")) {
                    weatherRq = getWeatherFromSite("Sankt-Peterburg");
                } else weatherRq = getWeatherFromSite(city);
                if (weatherRq.getTemp() != null) {
                    City cityEntity = new City();
                    cityEntity.setTemp(weatherRq.getTemp());
                    cityEntity.setClouds(weatherRq.getClouds());
                    cityEntity.setTitle(city);
                    cityEntity.setCountryId(1);
                    cityRepository.saveOrUpdate(cityEntity);
                } else log.info(String.format("An error occurred while getting the weather from '%s'", city));
            }
            log.info("Weather in cities has been updated");
        } else {
            log.info("Parsing disabled");
        }
    }

    private Set<String> getAllCity() {
        var personList = personRepository.findAll();
        Set<String> cityList = new HashSet<>();
        for (Person person : personList) {
            String city = person.getCity();
            if (city != null && !city.equals("")) {
                cityList.add(city);
            }
        }
        return cityList;
    }
}
