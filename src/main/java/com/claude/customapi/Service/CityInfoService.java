package com.claude.customapi.Service;

import com.claude.customapi.Exception.CityInfoException;
import com.claude.customapi.Interface.CityInfoInterface;
import com.claude.customapi.Model.CityData;
import com.claude.customapi.Repository.CityInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CityInfoService implements CityInfoInterface {

    @Value("${openweather.api.key}")
    private String apiKey;
    @Value("${openweather.api.forecast.uri}")
    private String cityAPIURL;
    @Autowired
    private CityInfoRepository cityInfoRepository;

    @Override
    @Tool(description = "Add new city")
    public CityData addNewCity(String cityName) throws JSONException {
        log.info("Add new city {}", cityName);
        //Saving all City Details in Postgresql DB after fetching JSON response from Open Weather application
        if (cityInfoRepository.existsByCityName(cityName))
            throw new CityInfoException("Save operation aborted since city is already existed in database..");
        else {
            String openWeatherURI = cityAPIURL.concat(cityName).concat("&mode=json&appid=").concat(apiKey).concat("&units=metric");
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject(openWeatherURI, String.class);
            JSONObject jsonObject = new JSONObject(jsonResponse).getJSONObject("city");
            CityData cityData = CityData.builder()
                    .cityId(jsonObject.getLong("id"))
                    .cityName(jsonObject.get("name").toString())
                    .country(jsonObject.get("country").toString())
                    .latitude(jsonObject.getJSONObject("coord").getLong("lat"))
                    .longitude(jsonObject.getJSONObject("coord").getLong("lon"))
                    .population(jsonObject.getLong("population"))
                    .timeZone(jsonObject.getLong("timezone"))
                    .sunRise(jsonObject.getLong("sunrise"))
                    .sunSet(jsonObject.getLong("sunset"))
                    .build();
            return cityInfoRepository.saveAndFlush(cityData);
        }
    }

    @Override
    @Tool(description = "Fetech all available cities from database")
    public List<CityData> fetchAllCityDetails() {
        log.info("List out all cities available in database");
        //Sorting all city details by its population
        return cityInfoRepository.findAll().stream().sorted((o1, o2) -> (o1.getPopulation() > o2.getPopulation()) ? (-1) : (1)).collect(Collectors.toList());/**/
    }

    @Override
    @Tool(description = "Fetch specific city info by id")
    public CityData fetchCityById(long id) {
        log.info("Fetch specific city by ID");
        //Fetching city details by its ID
        if (cityInfoRepository.existsById(id))
            return cityInfoRepository.findById(id).get();
        else
            throw new CityInfoException("Get operation aborted since, City ID doesn't exist in database..");
    }

    @Override
    @Tool(description = "Fetch specific city by name")
    public CityData fetchCityByName(String cityName) throws JSONException {
        log.info("Fetch specific city by cityName");
        //Fetching city details by city name
        if (cityInfoRepository.existsByCityName(cityName))
            return cityInfoRepository.findByCityName(cityName);
        else
            return this.addNewCity(cityName);
    }

    @Override
    @Tool(description = "Delete city info from database")
    public String deleteCityDetails(String cityName) {
        log.info("Delete specific city by cityName");
        //Deleting city details by city name
        if (cityInfoRepository.existsByCityName(cityName)) {
            cityInfoRepository.deleteById(cityInfoRepository.findByCityName(cityName).getCityId());
            return cityName.concat(" city has been deleted from database successfully..");
        } else
            throw new CityInfoException("Delete operation aborted since, City Name doesn't exist in database..");
    }
}
