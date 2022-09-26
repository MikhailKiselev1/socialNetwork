package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.entity.Country;
import org.javaproteam27.socialnetwork.repository.CountryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {
    
    private final CountryRepository countryRepository;
    
    
    public Country findById(int id) {
        return countryRepository.findById(id);
    }
    
}
