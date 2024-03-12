package io.unbong.ubrpc.demo.api;



public interface UserService {

    User findById(int id);

    int findId();

    String findName(int id);
}
