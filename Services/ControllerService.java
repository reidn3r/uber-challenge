package com.example.uber.Services;

import com.example.uber.DTO.ExternalResponseDTO;
import com.example.uber.DTO.FoodTruckDTO;
import com.example.uber.DTO.UserLocationDTO;
import com.example.uber.DTO.LocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ControllerService {

    @Autowired
    ExternalApiService apiService;

    public ResponseEntity<List<FoodTruckDTO>> findByRadius(UserLocationDTO data) throws Exception{
        if(data.radius() < 0) throw new Exception("Distância inválida");

        List<ExternalResponseDTO> foodTrucks = this.apiService.getAllFoodTrucks();
        Stream<ExternalResponseDTO> ftStream = foodTrucks.stream();

        List<FoodTruckDTO> stream = ftStream.filter(ft -> this.CalculateDistanceBetweenLocationsInMeters(
                new LocationDTO(Double.parseDouble(ft.latitude), Double.parseDouble(ft.longitude)),
                new LocationDTO(data.latitude(), data.longitude())) < data.radius())
                .filter(ft -> ft.status.equalsIgnoreCase("APPROVED") && ft.getX() != null && ft.getY() != null)
                .map(FoodTruckDTO::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.OK).body(stream);
    }

    private double CalculateDistanceBetweenLocationsInMeters(LocationDTO loc1, LocationDTO loc2){
        /*
        * Calcula a distância em metros entre 2 pontos de acordo com
        * a latitude e longitude de cada um
        * Metodo: Fórmula de haversine
        */
        double lat1 = loc1.getLatitude() * Math.PI/180;
        double lat2 = loc2.getLatitude() * Math.PI/180;

        double deltaLat = (loc2.getLatitude() - loc1.getLatitude()) * Math.PI/180;
        double deltaLon = (loc2.getLongitude() - loc1.getLongitude()) * Math.PI/180;

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6371 * 1000 * c; //metros
    }
}
