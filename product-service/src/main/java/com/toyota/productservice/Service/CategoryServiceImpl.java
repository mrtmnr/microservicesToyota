package com.toyota.productservice.Service;


import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public  class CategoryServiceImpl implements CategoryService{

    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category findByTitle(String title) {

        Optional<Category> category=categoryRepository.findByTitle(title);

        if (category.isPresent()){
            return category.get();
        }
        else {
            log.error("Product retrieval failed. Product not found with title: {}", title);
            throw new RuntimeException("Error: there is no such a category !");
        }
    }
}
