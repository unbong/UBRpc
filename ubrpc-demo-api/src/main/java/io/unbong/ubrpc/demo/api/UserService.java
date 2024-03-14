package io.unbong.ubrpc.demo.api;



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


}
