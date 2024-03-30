package io.unbong.ubrpc.demo.provider;

import io.unbong.ubrpc.core.annotation.UbProvider;
import io.unbong.ubrpc.demo.api.User;
import io.unbong.ubrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * todo
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 */
@Component
@UbProvider
public class UserServiceImpl implements UserService {

    @Autowired
    Environment environment;
    @Override
    public User findById(int id) {

        if (id == 404){
            throw new RuntimeException("404 exception");
        }
        
        return new User(100, "ubnong: " +environment.getProperty("server.port")
                + "_" + System.currentTimeMillis());
    }



    @Override
    public int findId() {
        return 100;
    }

    @Override
    public String findName(int id) {
        return "ubnong: " + System.currentTimeMillis();
    }

    @Override
    public User findByid(User user) {
        return new User(100, "ubnong  findByid(User user): " + System.currentTimeMillis());
    }

    @Override
    public User find(int id, String name) {
        return new User(100, "ubnong  findByid(int id, String name): " + System.currentTimeMillis());
    }

    @Override
    public String getName() {
        return "getName()";
    }

    @Override
    public String getName(int id) {
        return "getName(int id)";
    }

    @Override
    public long getId(long id) {
        return 2024L;
    }

    @Override
    public long getId(float id) {
        return (long) ((long)100L+id);
    }

    @Override
    public long getId(User user) {
        return Long.valueOf(user.getId());
    }

    @Override
    public long[] getIds(long[] ids) {
        return ids;
    }

    @Override
    public List<Long> getIds(List<Long> ids) {

        return ids;
    }

    @Override
    public List<User> getUsers(List<User> users) {
        return users;
    }

    @Override
    public Map<String, User> getUsers(Map<String, User> users) {
        return users;
    }

    @Override
    public User findTw(int timeout) {
        String port = environment.getProperty("server.port");
        if("8081".equals(port))
        {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new User(1001, "timeout-"+ port);
    }

}
