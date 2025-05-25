package com.example.nplus1test.domain.country.repository;

import com.example.nplus1test.domain.country.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

    Optional<CityEntity> findByCity(String city);
}
