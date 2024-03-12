package io.unbong.ubrpc.demo.provider;

import io.unbong.ubrpc.core.annotation.UbProvider;
import io.unbong.ubrpc.demo.api.User;
import io.unbong.ubrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
@Component
@UbProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {

        if (id == 404){
            throw new RuntimeException("404 exception");
        }

        return new User(100, "ubnong: " + System.currentTimeMillis());
    }

    @Override
    public int findId() {
        return 100;
    }

    @Override
    public String findName(int id) {
        return "ubnong: " + System.currentTimeMillis();
    }
}
