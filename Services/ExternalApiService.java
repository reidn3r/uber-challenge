package com.example.uber.Services;

import com.example.uber.DTO.ExternalResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ExternalApiService {
    @Autowired
    RestTemplateService restTemplateService;

    @Autowired
    RedisTemplate<String, List<ExternalResponseDTO>> redisTemplate;

    public List<ExternalResponseDTO> getAllFoodTrucks() throws Exception{
        String key = "foodtruck-list";
        List<ExternalResponseDTO> memo = redisTemplate.opsForValue().get(key);

        if(memo == null){
            String BaseUrl = "https://data.sfgov.org/resource/rqzj-sfat.json";
            RestTemplate client = this.restTemplateService.NewRestTemplate();
            ResponseEntity<String> response = client.getForEntity(BaseUrl, String.class);

            if(!response.getStatusCode().equals(HttpStatus.OK)){
                throw new Exception("Erro ao comunicar com API externa");
            }
            memo = this.SerializeApiResponse(response);
            redisTemplate.opsForValue().set(key, memo, Duration.ofDays(1));
        }
        return memo;
    }

    private List<ExternalResponseDTO> SerializeApiResponse(ResponseEntity<String> response){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ExternalResponseDTO[] responseBody = objectMapper.readValue(response.getBody(), ExternalResponseDTO[].class);
            return Arrays.asList(responseBody);
        }
        catch(IOException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
