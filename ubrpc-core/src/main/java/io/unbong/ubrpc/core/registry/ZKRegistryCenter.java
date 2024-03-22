package io.unbong.ubrpc.core.registry;

import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.meta.ServiceMeta;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Description
 *
 *  1 集成zookeeper客户端
 *      curator-client  curator-recipier
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 19:42
 */
public class ZKRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;


    @Value("${ubrpc.zkServer}")
    private String server;

    @Value("${ubrpc.zkRoot}")
    private String root;


    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(server)
                .namespace(root)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        System.out.println("ZKRegistryCenter----> started to server ["+ server + "/" + root +"]");
    }

    @Override
    public void stop() {

        client.close();
        System.out.println("ZKRegistryCenter----> stoped");
    }

    /**
     *
     * @param service  服务 　　　　全类名   注册为持久节点
     * @param instance 当前节点            注册为临时节点
     */
    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String serverPath ="/" + service.toPath();
        try {
            if(client.checkExists().forPath(serverPath) == null){
                // 创建服务的持久化的节点
                client.create().withMode(CreateMode.PERSISTENT).forPath(serverPath,"service".getBytes());
            }

            // 创建实例的临时节点
            String instancePath = serverPath + "/"+instance.toPath();

            System.out.println("zkregister registering----->" + instancePath);

            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            System.out.println("zkregister registered----->" + instance);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

        String serverPath ="/" + service.toPath();;

        try {
            if(client.checkExists().forPath(serverPath) == null){
                return;
            }
            // 删除实例的临时节点
            String instancePath = serverPath + "/"+instance.toPath();
            System.out.println("zkregister unregistering----->" + instancePath);
            client.delete().quietly().forPath(instancePath);
            System.out.println("zkregister unregistered----->" + instance);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String serverPath ="/" + service.toPath();;

        try {
            // 获取所有子节点
            List<String> nodes= client.getChildren().forPath(serverPath);
            System.out.println("fetch ALL from zk" +serverPath);
            nodes.forEach(System.out::println);

            List<InstanceMeta> providers = mapInstance(nodes);
            return providers;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }



    @NotNull
    private static List<InstanceMeta> mapInstance(List<String> nodes) {
        return nodes.stream().map(x->{
            String[] ip_port = x.split("_");
            return InstanceMeta.http(ip_port[0], Integer.valueOf(ip_port[1]));
        }).collect(Collectors.toList());
    }


    /**
     * 订阅服务发生变化时获取相应的变化
     *
     * @param service
     * @param listener    监听者用于修改结构
     */
    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        String servicePath = "/" + service.toPath();;
        // 有缓存 深度为2
        final TreeCache cache =  TreeCache.newBuilder(client, servicePath)
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        cache.getListenable().addListener(
                (curator, event)->{
                    // 当注册中心有变动时，这里会执行
                    System.out.println("zk subscribe event: " + event);
                    // get newest node info
                    List<InstanceMeta> nodes = fetchAll(service);
                    // publish data
                    listener.fire(new Event(nodes));
                }
        );
        cache.start();

    }
}
