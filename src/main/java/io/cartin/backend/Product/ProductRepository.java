package io.cartin.backend.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);

    // To enable this feature, must run `CREATE EXTENSION fuzzystrmatch;` on the db.
    @Query(value = "SELECT *, LEVENSHTEIN(product_name, :name) FROM cartin_products ORDER BY LEVENSHTEIN(product_name, :name) ASC", nativeQuery = true)
    List<Product> findByFuzzyName(String name);

    List<Product> findByCategoryAndPriceBetween(Category category, double min, double max);

}
