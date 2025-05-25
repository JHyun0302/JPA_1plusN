package com.example.nplus1test.domain.country.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    private CountryEntity countryEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReligionEntity religionEntity;
}
