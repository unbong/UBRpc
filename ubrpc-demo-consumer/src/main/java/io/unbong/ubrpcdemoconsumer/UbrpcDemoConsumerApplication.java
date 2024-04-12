package io.unbong.ubrpcdemoconsumer;

import io.unbong.ubrpc.core.annotation.UBConsumer;
import io.unbong.ubrpc.core.api.Router;
import io.unbong.ubrpc.core.api.RpcContext;
import io.unbong.ubrpc.core.cluster.GrayRouter;
import io.unbong.ubrpc.core.consumer.ConsumerConfig;
import io.unbong.ubrpc.demo.api.User;
import io.unbong.ubrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    Router router;

    @RequestMapping("/")
    public User invoke(@RequestParam("id")int id){
        return userService.findById(id);
    }

    @RequestMapping("/garay/")
    public String grayRatio(@RequestParam("ratio") int ratio){

        ((GrayRouter)router).setGrayRatio(ratio);
        return  "gray ratio is "+ ratio;
    }
    @RequestMapping("/find/")
    public User find(@RequestParam("timeout") String timeout){

        return userService.findTw(Integer.valueOf(timeout).intValue());
    }

    @RequestMapping("/test/")
    public String testData(){
        test1();
        return "ok";
    }

    private void test1(){
        long ul = userService.getId(new User(101, "unbong"));
        log.info("getId(user) "+ ul);

        log.info("test getMap.");
        Map<String, User> user_map= new HashMap<>();
        user_map.put("one", new User(1,"one"));
        Map<String, User> res_getMap = userService.getMap(user_map);

        res_getMap.forEach((k,v)->{
            log.info("key: {}, value: {}", k, v);
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(UbrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner consumer_runner11()
    {
        return x->{


//            log.info("test getMap.");
//            Map<String, User> user_map= new HashMap<>();
//            user_map.put("one", new User(1,"one"));
//            Map<String, User> res_getMap = userService.getMap(user_map);
//
//            res_getMap.forEach((k,v)->{
//                log.info("key: {}, value: {}", k, v);
//            });
            test();

        };
    }


    private void test() {
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

        try{
            User user12 = userService.findById(404);
           log.info(user12.toString());
        }
        catch (Exception e)
        {
           log.error("exception test case {}", e.getCause().getMessage());
        }

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

        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        List<Long> list = userService.getIds(ids);
        //list.forEach(log::info);

        List<User> users = new ArrayList<>();
        users.add(new User(100,"100"));
        users.add(new User(101,"101"));
        users.add(new User(102,"102"));
        List<User> res1 = userService.getUsers(users);
        res1.forEach(x->log.info(x.toString()));

        Map<String, User> userMap= new HashMap<>();
        userMap.put("one", new User(1,"one"));
        userMap.put("two", new User(1,"two"));
        userMap.put("three", new User(1,"three"));

        userMap.forEach((k,v)->log.info(k+v.toString()));


        long start = System.currentTimeMillis();
        User userTw = userService.findTw(2000);
        log.info("userService#find take" + (System.currentTimeMillis() - start)+"ms");


        log.info("test getMap.");
        Map<String, User> user_map= new HashMap<>();
        user_map.put("one", new User(1,"one"));
        Map<String, User> res_getMap = userService.getMap(user_map);

        res_getMap.forEach((k,v)->{
            log.info("key: {}, value: {}", k, v);
        });

    }
}
