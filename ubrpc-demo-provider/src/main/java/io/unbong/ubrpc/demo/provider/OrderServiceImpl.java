package io.unbong.ubrpc.demo.provider;

import io.unbong.ubrpc.demo.api.Order;
import io.unbong.ubrpc.demo.api.OrderService;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-10 21:54
 */
public class OrderServiceImpl implements OrderService {

    public Order findById(int id){
        if (id == 404){
            throw new RuntimeException("404 exception");
        }

        return new Order();
    }
}
