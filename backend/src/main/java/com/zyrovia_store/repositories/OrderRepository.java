package com.zyrovia_store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zyrovia_store.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUser_Id(Long userId);
	
	@Query("""
			SELECT DISTINCT o
			FROM Order o
			JOIN o.orderItems oi
			JOIN oi.product p
			WHERE p.seller.id = :sellerId
			""")
	List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);
}