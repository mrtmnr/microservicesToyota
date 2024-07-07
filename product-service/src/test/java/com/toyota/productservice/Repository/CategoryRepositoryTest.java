package com.toyota.productservice.Repository;

import com.toyota.productservice.Entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;



@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository underTest;

    @Test
    void shouldReturnCategoryWhenFindByTitle() {

        //given
        String title="title";
        Category category=new Category("categoryTitle");

        underTest.save(category);

        //when
        Optional<Category> optionalCategory= underTest.findByTitle(title);

        //then
        assertThat(optionalCategory).isPresent();
        assertThat(optionalCategory.get()).isEqualTo(category);



    }
}