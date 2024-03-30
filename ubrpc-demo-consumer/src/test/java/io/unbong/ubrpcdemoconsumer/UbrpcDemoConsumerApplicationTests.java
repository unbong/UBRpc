package io.unbong.ubrpcdemoconsumer;

import io.unbong.ubrpc.core.test.TestZkServer;
import io.unbong.ubrpc.demo.provider.UbrpcDemoProviderApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class UbrpcDemoConsumerApplicationTests {


    static TestZkServer testZkServer = new TestZkServer();

    static ApplicationContext context;

    static ApplicationContext context2;

    @BeforeAll

    static void init(){
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        testZkServer.start();

        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8094    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context = SpringApplication.run(UbrpcDemoProviderApplication.class,
                "--server.port=8094", "--kkrpc.zkServer=localhost:2182",
                "--logging.level.cn.kimmking.kkrpc=info","--ubrpc.zkRoot=Ubrpc"
                ,"--app.id=app1","--app.namespace=public","--app.env=dev");

        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8095    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");

        context2 = SpringApplication.run(UbrpcDemoProviderApplication.class,
                "--server.port=8095", "--kkrpc.zkServer=localhost:2182",
                "--logging.level.cn.kimmking.kkrpc=info","--ubrpc.zkRoot=Ubrpc"
                ,"--app.id=app1","--app.namespace=public","--app.env=dev");

    }

    @Test
    void contextLoads() {
        Assertions.assertEquals("2", "2");
    }

    static void stop()
    {
        SpringApplication.exit(context,()->1);
        SpringApplication.exit(context2,()->1);
        testZkServer.stop();
    }


}
