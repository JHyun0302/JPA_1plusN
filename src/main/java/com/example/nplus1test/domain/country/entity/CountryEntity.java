package com.example.nplus1test.domain.country.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Setter
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String country;

    //페이지네이션 문제 해결 batch in절
    @BatchSize(size = 250)
    @OneToMany(mappedBy = "countryEntity")
    private List<CityEntity> cityEntities = new ArrayList<>();

    public void addCityEntity(CityEntity cityEntity) {
        cityEntities.add(cityEntity);
        cityEntity.setCountryEntity(this);
    }

    //다중 OneToMany fetch 문제
    @BatchSize(size = 250)
    @OneToMany(mappedBy = "countryEntity")
    public List<ReligionEntity> religionEntities = new ArrayList<>();

    public void addReligionEntity(ReligionEntity religionEntity) {
        religionEntities.add(religionEntity);
        religionEntity.setCountryEntity(this);
    }
}
