package com.example_project_name.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example_project_name.dto.UserDTO;

public interface UserRepository extends JpaRepository<UserDTO, String> {
    List<UserDTO> findByName(String name); // Spring generates the query for you
}

