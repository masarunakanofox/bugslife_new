package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Shop;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopRepository extends JpaRepository<Shop, Long> {
	@Query("SELECT s FROM Shop s WHERE UPPER(s.name) LIKE CONCAT('%', UPPER(:name), '%')")
	List<Shop> findByCaseInsensitiveNameContaining(@Param("name") String name);
}