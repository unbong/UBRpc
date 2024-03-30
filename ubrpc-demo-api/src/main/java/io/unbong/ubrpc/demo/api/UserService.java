package io.unbong.ubrpc.demo.api;


import java.util.List;
import java.util.Map;

public interface UserService {

    User findById(int id);

    int findId();

    String findName(int id);

    User findByid(User user);

    User find(int id , String name);

    String getName();

    String getName(int id);

    long getId(long id);

    long getId(float id);

    long getId(User user);

    long[] getIds(long[] ids);

    List<Long> getIds(List<Long> ids);

    List<User> getUsers(List<User> users);

    Map<String , User> getUsers(Map<String, User> users);

    User findTw(int timeout);
}
