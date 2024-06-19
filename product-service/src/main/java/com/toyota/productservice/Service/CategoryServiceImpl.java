package com.toyota.productservice.Service;


import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService{

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
            throw new RuntimeException("Error: there is no such a category !");
        }
    }
}
