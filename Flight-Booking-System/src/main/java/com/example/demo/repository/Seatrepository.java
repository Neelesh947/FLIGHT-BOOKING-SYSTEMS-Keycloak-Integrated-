package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Seat;

@Repository
public interface Seatrepository extends JpaRepository<Seat,Long > {

}
