package io.unbong.ubrpc.core.registry.ub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.unbong.ubrpc.core.api.RegistryCenter;
import io.unbong.ubrpc.core.consumer.HttpInvoker;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.meta.ServiceMeta;
import io.unbong.ubrpc.core.registry.ChangedListener;
import io.unbong.ubrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * UB registry center
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-04-27 13:03
 */
@Slf4j
public class UbRegistryCenter implements RegistryCenter {

    public static final String REG = "/reg";
    public static final String FIND_ALL = "/findAll";
    public static final String UNREG = "/unreg";
    public static final String VERSION = "/version";
    @Value("${ubregistry.servers}")
    String servers;

    UbHealthChecker healthChecker = new UbHealthChecker();
    MultiValueMap<InstanceMeta,ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    @Override
    public void start() {
        log.info("----> [UBRegisry] start with server {}", servers);
        healthChecker.start();
        providerCheck();
    }

    @Override
    public void stop() {
        log.info("----> [UBRegisry] stoped with server {}", servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info("----> [UBRegisry] regist instance {} for {}", instance, servers);
        InstanceMeta instanceMeta = HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), InstanceMeta.class);
        log.info("----> [UBRegisry] registed instance {} ", instanceMeta);
        RENEWS.add(instance,service);
    }



    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info("----> [UBRegisry] unregiste instance {} for {}", instance, servers);
        InstanceMeta instanceMeta = HttpInvoker.httpPost(JSON.toJSONString(instance), unregPath(service), InstanceMeta.class);
        log.info("----> [UBRegisry] unregisted instance {} ", instanceMeta);
        RENEWS.remove(instance, service);
    }


    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta serviceName) {
        log.info("----> [UBRegisry] findall  {}", serviceName);
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(serviceName), new TypeReference<List<InstanceMeta>>(){});
        log.info("----> [UBRegisry] findall instances {} ", instances);
        return instances;
    }


    Map<String, Long> VERSIONS = new HashMap<>();


    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {

        log.info("----> [UBRegisry] subscribe service {}",service);
        healthChecker.consumerCheck(()->{
            try{

                Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
                Long newVer = HttpInvoker.httpGet(versionPath(service), Long.class);
                log.info("----> [UBRegisry] version:{} newVer {} for {}",version, newVer, servers);
                if(newVer > version){
                    List<InstanceMeta> intances = fetchAll(service);
                    listener.fire(new Event(intances));
                    VERSIONS.put(service.toPath(), newVer);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        });
    }



    public void providerCheck(){
        healthChecker.providerCheck(()->{
            RENEWS.keySet().stream().forEach(instance->{
                StringBuilder sb = new StringBuilder();
                for(ServiceMeta serviceMeta: RENEWS.get(instance))
                {
                    sb.append(serviceMeta.toPath()).append(",");
                }
                String services = sb.toString();
                if(services.endsWith(","))
                    services = services.substring(0, services.length()-1);
                Long timeStamp = HttpInvoker.httpPost(JSON.toJSONString(instance),servers+"/renews?service="+services, Long.class);
                log.info("----> [UBRegisry] renewed instance{} fpr {} at {}", instance, servers, timeStamp);
            });
        });
    }


    private String regPath(ServiceMeta service) {
        return path(REG, service);
    }

    private String findAllPath(ServiceMeta service) {
        return  path(FIND_ALL, service);
    }


    private String unregPath(ServiceMeta service) {
        return path(UNREG, service);
    }


    private String versionPath(ServiceMeta service) {
        return  path(VERSION, service);
    }

    private String path(String context, ServiceMeta service){
        return servers + context + "?service=" + service.toPath();
    }
}
