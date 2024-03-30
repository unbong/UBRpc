package io.unbong.ubrpcdemoconsumer;

import io.unbong.ubrpc.core.test.EmbedZookeeperServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class UbrpcDemoConsumerApplicationTests {


    static EmbedZookeeperServer embedZookeeperServer;

    static ApplicationContext context;

    @BeforeAll

    static void init(){
        embedZookeeperServer = new EmbedZookeeperServer();
    }

    @Test
    void contextLoads() {
        Assertions.assertEquals("2", "2");
    }

    static void stop()
    {

    }


}
