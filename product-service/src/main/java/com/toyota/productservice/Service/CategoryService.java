package com.toyota.productservice.Service;


import com.toyota.productservice.Entity.Category;

public interface CategoryService {
   Category findByTitle(String title);
}
