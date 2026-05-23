package com.EasyFix.repository;

import com.EasyFix.model.ServiceCategory;
import com.EasyFix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory,Long> {
    Optional<ServiceCategory>  findByName(String name);
}
