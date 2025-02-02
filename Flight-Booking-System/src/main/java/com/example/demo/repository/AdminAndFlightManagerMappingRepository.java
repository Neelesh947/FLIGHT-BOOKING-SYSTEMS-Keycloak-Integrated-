package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.AdminAndFlightManagerMapping;

@Repository
public interface AdminAndFlightManagerMappingRepository extends JpaRepository<AdminAndFlightManagerMapping, String>{

	public List<AdminAndFlightManagerMapping> findByAdminId(String adminId);
}
