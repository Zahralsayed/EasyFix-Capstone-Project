package com.EasyFix.service;

import com.EasyFix.model.ServiceCategory;
import com.EasyFix.repository.ServiceCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoryService {
    private final ServiceCategoryRepository categoryRepository;

    public ServiceCategoryService(ServiceCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public ServiceCategory createCategory(ServiceCategory category) {
        if (categoryRepository.findByName(category.getName()).isPresent()){
            throw new RuntimeException("Category " + category.getName() + " already exists");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id){
        if (! categoryRepository.existsById(id)){
            throw new RuntimeException("Category with ID " + id + " does not exist");
        }
        categoryRepository.deleteById(id);
    }
}
