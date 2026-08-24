package com.zyrovia_store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zyrovia_store.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
