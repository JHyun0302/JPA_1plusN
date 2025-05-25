package com.example.nplus1test.controller;

import com.example.nplus1test.domain.country.service.CountryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReadController {

    private final CountryService countryService;

    public ReadController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/read/one/parent")
    public String readOneParent(Model model) {

        model.addAttribute("COUNTRYLIST", countryService.readCountry());

        return "readOneParent";
    }

    @GetMapping("/read/one/child")
    public String readOneChild(Model model) {

        model.addAttribute("COUNTRYLIST", countryService.readCountry());

        return "readOneChild";
    }

    @GetMapping("/read/many/child/{cityname}")
    public String readManyChild(Model model, @PathVariable("cityname") String cityname) {

        model.addAttribute("CITY", countryService.readCity(cityname));

        return "readManyChild";
    }

    @GetMapping("/read/one/child/fetch")
    public String readOneChildFetch(Model model) {

        model.addAttribute("COUNTRYLIST", countryService.readCountryFetch());

        return "readOneChild";
    }
}
