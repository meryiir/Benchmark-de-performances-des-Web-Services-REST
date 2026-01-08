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
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    
    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public Page<Item> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }
    
    public Page<Item> findByCategoryId(Long categoryId, Pageable pageable) {
        return itemRepository.findByCategoryId(categoryId, pageable);
    }
    
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }
    
    public Optional<Item> save(Item item) {
        // Verify and set category
        if (item.getCategory() != null && item.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(item.getCategory().getId());
            if (category.isEmpty()) {
                return Optional.empty();
            }
            item.setCategory(category.get());
        }
        return Optional.of(itemRepository.save(item));
    }
    
    public Optional<Item> update(Long id, Item itemDetails) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return Optional.empty();
        }
        
        Item item = optionalItem.get();
        item.setSku(itemDetails.getSku());
        item.setName(itemDetails.getName());
        item.setPrice(itemDetails.getPrice());
        item.setStock(itemDetails.getStock());
        
        // Update category if provided
        if (itemDetails.getCategory() != null && itemDetails.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(itemDetails.getCategory().getId());
            if (category.isEmpty()) {
                return Optional.empty(); // Invalid category
            }
            item.setCategory(category.get());
        }
        
        return Optional.of(itemRepository.save(item));
    }
    
    public boolean deleteById(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
