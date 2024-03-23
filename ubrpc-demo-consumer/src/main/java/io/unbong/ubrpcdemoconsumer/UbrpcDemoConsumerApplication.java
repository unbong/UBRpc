package io.unbong.ubrpcdemoconsumer;

import io.unbong.ubrpc.core.annotation.UBConsumer;
import io.unbong.ubrpc.core.api.RpcResponse;
import io.unbong.ubrpc.core.consumer.ConsumerConfig;
import io.unbong.ubrpc.demo.api.User;
import io.unbong.ubrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

            log.info("consumer application running");
            User user = userService.findById(1);


            log.info("user ====> "+user);


            int id = userService.findId();
            log.info("findId ====> "+ id);

            String name = userService.findName(1);
            log.info("findName ====> "+ name);

            String toString = userService.toString();
            log.info("toString ====> "+ toString);


            User ss = userService.find(1, "");
            log.info(ss.toString());

            log.info(userService.getName());

            log.info(userService.getName(11));

//            User user1 = userService.findById(404);
//            log.info(user1);

            User usernull = userService.findByid(null);
            log.info("user null  ====> "+ usernull);



            long user_long = userService.getId(102L);
            log.info("getId(long) ====> "+ user_long);

            long ul = userService.getId(new User(101, "unbong"));
            log.info("getId(user) "+ ul);

            long res = userService.getId(10.0f);
            log.info("getId(float) "+ res);

            long[] resArray = userService.getIds(new long[]{1L,2l,3l});

            for(long l : resArray)
            {
                log.info(""+l);
            }

//            List<Long> ids = new ArrayList<>();
//            ids.add(1L);
//            ids.add(2L);
//            ids.add(3L);
//            List<Long> list = userService.getIds(ids);
//            list.forEach(log::info);

//            List<User> users = new ArrayList<>();
//            users.add(new User(100,"100"));
//            users.add(new User(101,"101"));
//            users.add(new User(102,"102"));
//            List<User> res = userService.getUsers(users);
//            res.forEach(log::info);
//
//            Map<String, User> userMap= new HashMap<>();
//            userMap.put("one", new User(1,"one"));
//            userMap.put("two", new User(1,"two"));
//            userMap.put("three", new User(1,"three"));
//
//            userMap.forEach((k,v)->log.info(k+v.toString()));

        };
    }
}
