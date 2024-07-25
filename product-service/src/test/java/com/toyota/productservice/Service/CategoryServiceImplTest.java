package com.toyota.productservice.Service;


import com.toyota.productservice.Entity.Category;
import com.toyota.productservice.Repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;


import java.util.Optional;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    CategoryService underTest;

    @Mock
    CategoryRepository categoryRepository;


    @BeforeEach
    void setUp() {
        underTest=new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void shouldReturnCategoryByTitle() {
        //given
        Category category=new Category("fruit");
        category.setId(1);

        when(categoryRepository.findByTitle("fruit")).thenReturn(Optional.of(category));
        //when
        Category optionalCategory=underTest.findByTitle("fruit");
        //then
        assertThat(optionalCategory).isEqualTo(category);
    }

    @Test
    void shouldThrowExceptionCategoryByInvalidTitle() {
        assertThatThrownBy(()->
                underTest.findByTitle("invalid-title"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error: there is no such a category !");
    }
}
