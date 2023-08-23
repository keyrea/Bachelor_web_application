package com.example.demo.repository;

import com.example.demo.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ServiceRepository  extends JpaRepository<Services,Long> {
}
