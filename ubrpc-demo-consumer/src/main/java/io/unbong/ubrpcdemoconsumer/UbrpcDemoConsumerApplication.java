package io.unbong.ubrpcdemoconsumer;

import io.unbong.ubrpc.core.annotation.UBConsumer;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.consumer.ConsumerConfig;
import io.unbong.ubrpc.demo.api.User;
import io.unbong.ubrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
public class UbrpcDemoConsumerApplication {


    @UBConsumer
    UserService userService;

    @RequestMapping("/")
    public User invoke(int id){
        return userService.findById(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(UbrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner consumer_runner11()
    {
        return x->{
//
//            System.out.println("consumer application running");
//            User user = userService.findById(1);
//
//
//            System.out.println("user ====> "+user);
//
//
//            int id = userService.findId();
//            System.out.println("findId ====> "+ id);
//
//            String name = userService.findName(1);
//            System.out.println("findName ====> "+ name);
//
//            String toString = userService.toString();
//            System.out.println("toString ====> "+ toString);
//
//
//            User ss = userService.find(1, "");
//            System.out.println(ss);
//
//            System.out.println(userService.getName());
//
//            System.out.println(userService.getName(11));
//
////            User user1 = userService.findById(404);
////            System.out.println(user1);
//
//            User usernull = userService.findByid(null);
//            System.out.println("user null  ====> "+ usernull);
//
//
//
//            long user_long = userService.getId(102L);
//            System.out.println("getId(long) ====> "+ user_long);
//
//            long ul = userService.getId(new User(101, "unbong"));
//            System.out.println("getId(user) "+ ul);
//
//            long res = userService.getId(10.0f);
//            System.out.println("getId(float) "+ res);
//
//            long[] resArray = userService.getIds(new long[]{1L,2l,3l});
//
//            for(long l : resArray)
//            {
//                System.out.println(l);
//            }

//            List<Long> ids = new ArrayList<>();
//            ids.add(1L);
//            ids.add(2L);
//            ids.add(3L);
//            List<Long> list = userService.getIds(ids);
//            list.forEach(System.out::println);
//
//            Map<String, User> userMap= new HashMap<>();
//            userMap.put("one", new User(1,"one"));
//            userMap.put("two", new User(1,"two"));
//            userMap.put("three", new User(1,"three"));
//
//            userMap.forEach((k,v)->System.out.println(k+v.toString()));

        };
    }
}
