package com.example.order.controller;

import com.example.order.application.OrderCoordinator;
import com.example.order.application.OrderService;
import com.example.order.application.RedisLockService;
import com.example.order.application.dto.CreateOrderResult;
import com.example.order.controller.dto.CreateOrderRequest;
import com.example.order.controller.dto.CreateOrderResponse;
import com.example.order.controller.dto.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;

    private final OrderCoordinator orderCoordinator;

    private final RedisLockService redisLockService;

    public OrderController(OrderService orderService, OrderCoordinator orderCoordinator, RedisLockService redisLockService) {
        this.orderService = orderService;
        this.orderCoordinator = orderCoordinator;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResult result = orderService.createOrder(request.toCreateOrderCommand());

        return new CreateOrderResponse(result.orderId());
    }

    @PostMapping("/order/place")
    public void placeOrder(
            @RequestBody PlaceOrderRequest request
    ) {
        String key = "order:" + request.orderId();
        boolean acquiredLock = redisLockService.tryLock(key, request.orderId().toString());

        if (!acquiredLock) {
            throw new RuntimeException("락획득에 실패하였습니다.");
        }

        try {
            orderCoordinator.placeOrder(request.toCommand());
        } finally {
            redisLockService.releaseLock(key);
        }
    }
}
