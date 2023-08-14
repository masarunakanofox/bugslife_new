package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Shop;
import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
	List<Shop> findByNameContaining(String name);
}