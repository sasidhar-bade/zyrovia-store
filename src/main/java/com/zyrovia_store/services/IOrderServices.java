package com.zyrovia_store.services;

import java.util.List;

import com.zyrovia_store.dtos.OrderResponseDto;

public interface IOrderServices {

	OrderResponseDto placeOrder();

	List<OrderResponseDto> getMyOrders();

	OrderResponseDto getOrderById(Long orderId);

}
