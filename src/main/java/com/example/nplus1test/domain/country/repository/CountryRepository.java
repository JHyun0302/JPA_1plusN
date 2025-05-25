package com.example.nplus1test.domain.country.repository;

import com.example.nplus1test.domain.country.entity.CountryEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    Optional<CountryEntity> findByCountry(String country);

    @Query("SELECT co FROM CountryEntity co ")
//            "LEFT JOIN FETCH co.cityEntities ci") // @BatchSize 적용시, Lazy + Fetch 제거
//            "LEFT JOIN co.religionEntities re")
    List<CountryEntity> findAllFetch(Pageable pageable);

}
