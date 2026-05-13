package com.EasyFix.repository;

import com.EasyFix.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory,Long> {
}
