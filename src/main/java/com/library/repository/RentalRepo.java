package com.library.repository;

import com.library.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepo extends JpaRepository<Rental, Long> {
    List<Rental> findByClientId(Long id);
}

