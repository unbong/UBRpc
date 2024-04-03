package io.unbong.ubrpc.core.registry.zk;

import com.alibaba.fastjson.JSON;
import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.api.RpcException;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.meta.ServiceMeta;
import io.unbong.ubrpc.core.registry.ChangedListener;
import io.unbong.ubrpc.core.registry.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Slf4j
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
        log.info("ZKRegistryCenter----> started to server ["+ server + "/" + root +"]");
    }

    @Override
    public void stop() {

        client.close();
        log.info("ZKRegistryCenter----> stoped");
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
                client.create().withMode(CreateMode.PERSISTENT).forPath(serverPath,service.toMetas().getBytes());
            }

            // 创建实例的临时节点
            String instancePath = serverPath + "/"+instance.toPath();

            log.info("zkregister registering----->" + instancePath);

            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instance.toMetas().getBytes());
            log.info("zkregister registered----->" + instance);
        } catch (Exception e) {

            throw new RpcException(e);
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
            log.info("zkregister unregistering----->" + instancePath);
            client.delete().quietly().forPath(instancePath);
            log.info("zkregister unregistered----->" + instance);
        } catch (Exception e) {

            throw new RpcException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String serverPath ="/" + service.toPath();;

        try {
            // 获取所有子节点
            List<String> nodes= client.getChildren().forPath(serverPath);
            log.info("fetch ALL from zk" +serverPath);
            nodes.forEach(log::info);

            List<InstanceMeta> providers = mapInstance(nodes, serverPath);
            return providers;

        } catch (Exception e) {

            throw new RpcException(e);
        }

    }



    @NotNull
    private  List<InstanceMeta> mapInstance(List<String> nodes, String serverPath) {
        return nodes.stream().map(x->{

            String[] ip_port = x.split("_");
            InstanceMeta instanceMeta = InstanceMeta.http(ip_port[0], Integer.valueOf(ip_port[1]));

            // 子节点路径
            String nodePath = serverPath +"/" + x;
            // instance node path
            byte[] bytes;
            try {
                bytes = client.getData().forPath(nodePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Map<String,String> parameter = JSON.parseObject(new String(bytes), HashMap.class);
            parameter.forEach((k,v)->{
                log.debug("{} -> {}", k, v);
            });
            instanceMeta.setParameters(parameter);
            return instanceMeta;
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
                    log.info("zk subscribe event: " + event);
                    // get newest node info
                    List<InstanceMeta> nodes = fetchAll(service);
                    // publish data
                    listener.fire(new Event(nodes));
                }
        );
        cache.start();

    }
}
