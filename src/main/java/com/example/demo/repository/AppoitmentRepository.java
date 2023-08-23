package com.example.demo.repository;

import com.example.demo.model.Appoitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppoitmentRepository extends JpaRepository<Appoitment,Long> {
}
