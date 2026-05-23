package com.EasyFix.controller;

import com.EasyFix.model.ServiceCategory;
import com.EasyFix.repository.ServiceCategoryRepository;
import com.EasyFix.service.ServiceCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ServiceCategoryController {

    private final ServiceCategoryService categoryService;

    public ServiceCategoryController(ServiceCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceCategory>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody ServiceCategory category){
        try {
          ServiceCategory savedCategory = categoryService.createCategory(category);
          return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category successfully deleted.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
