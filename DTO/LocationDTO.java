package com.example.uber.DTO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationDTO implements Serializable {
    private double latitude;
    private double longitude;

    public LocationDTO(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
}
