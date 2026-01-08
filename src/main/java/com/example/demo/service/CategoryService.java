package com.example.demo.service;

import com.example.demo.domain.Category;
import com.example.demo.domain.Item;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    
    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }
    
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    public Optional<Category> update(Long id, Category categoryDetails) {
        return categoryRepository.findById(id).map(category -> {
            category.setCode(categoryDetails.getCode());
            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        });
    }
    
    public boolean deleteById(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Page<Item> findItemsByCategoryId(Long categoryId, Pageable pageable) {
        return itemRepository.findByCategoryId(categoryId, pageable);
    }
}
