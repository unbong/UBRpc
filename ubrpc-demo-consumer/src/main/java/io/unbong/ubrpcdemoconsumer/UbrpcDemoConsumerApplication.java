package io.unbong.ubrpcdemoconsumer;

import io.unbong.ubrpc.core.annotation.UBConsumer;
import io.unbong.ubrpc.core.consumer.ConsumerConfig;
import io.unbong.ubrpc.demo.api.User;
import io.unbong.ubrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class UbrpcDemoConsumerApplication {


    @UBConsumer
    UserService userService;


    public static void main(String[] args) {
        SpringApplication.run(UbrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner consumer_runner11()
    {
        return x->{
            System.out.println("consumer application running");
            User user = userService.findById(1);


            System.out.println("user ====> "+user);


            int id = userService.findId();
            System.out.println("findId ====> "+ id);

            String name = userService.findName(1);
            System.out.println("findName ====> "+ name);

            String toString = userService.toString();
            System.out.println("toString ====> "+ toString);
//
//            User user1 = userService.findById(404);
//            System.out.println(user1);
        };
    }
}
