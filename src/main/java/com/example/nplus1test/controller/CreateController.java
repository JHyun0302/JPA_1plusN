package com.example.nplus1test.controller;

import com.example.nplus1test.domain.country.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CreateController {

    private final CountryService countryService;

    public CreateController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/country")
    public String countryPage() {

        return "country";
    }

    @GetMapping("/create/country")
    public String countryProcess(@RequestParam(name= "country") String country) {

        countryService.createCountry(country);

        return "redirect:/country";
    }

    @GetMapping("/create/city")
    public String cityProcess(@RequestParam(name = "country") String country, @RequestParam(name = "city") String city) {

        countryService.createCity(country, city);

        return "redirect:/country";
    }

}
