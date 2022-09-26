package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.entity.City;
import org.javaproteam27.socialnetwork.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {
    
    private final CityRepository cityRepository;
    
    
    public City findByTitle(String city) {
        return cityRepository.findByTitle(city);
    }
    
}
