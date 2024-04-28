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

    @Value("${ubregistry.servers}")
    String servers;

    ScheduledExecutorService consumerExecutor;
    ScheduledExecutorService providerExecutor;
    MultiValueMap<InstanceMeta,ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    @Override
    public void start() {
        log.info("----> [UBRegisry] start with server {}", servers);
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor.scheduleWithFixedDelay(()->{
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
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        log.info("----> [UBRegisry] stoped with server {}", servers);
        consumerExecutor.shutdown();
        providerExecutor.shutdown();
        try {
            consumerExecutor.awaitTermination(1, TimeUnit.SECONDS);
            providerExecutor.awaitTermination(1, TimeUnit.SECONDS);
            if(!consumerExecutor.isTerminated()){
                log.debug("force terminate subscribe operation");
                consumerExecutor.shutdownNow();
            }
            if(!providerExecutor.isTerminated()){
                log.debug("force terminate renew operation");
                providerExecutor.shutdownNow();
            }

        } catch (InterruptedException e) {
            log.debug("terminate schedule failed");
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info("----> [UBRegisry] regist instance {} for {}", instance, servers);
        InstanceMeta instanceMeta = HttpInvoker.httpPost(JSON.toJSONString(instance), servers+"/reg?service="+ service.toPath(), InstanceMeta.class);
        log.info("----> [UBRegisry] registed instance {} ", instanceMeta);
        RENEWS.add(instance,service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info("----> [UBRegisry] unregiste instance {} for {}", instance, servers);
        InstanceMeta instanceMeta = HttpInvoker.httpPost(JSON.toJSONString(instance), servers+"/unreg?service="+ service.toPath(), InstanceMeta.class);
        log.info("----> [UBRegisry] unregisted instance {} ", instanceMeta);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta serviceName) {
        log.info("----> [UBRegisry] findall  {}", serviceName);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers+"/findAll?service="+serviceName.toPath(), new TypeReference<List<InstanceMeta>>(){});
        log.info("----> [UBRegisry] findall instances {} ", instances);
        return instances;
    }

    Map<String, Long> VERSIONS = new HashMap<>();


    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {

        log.info("----> [UBRegisry] subscribe service {}",service);
        consumerExecutor.scheduleWithFixedDelay(()->{
            try{

                Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
                Long newVer = HttpInvoker.httpGet(servers+ "/version?service="+service.toPath(), Long.class);
                log.info("----> [UBRegisry] version:{} newVer {} for {}",version, newVer, servers);
                if(newVer > version){
                    List<InstanceMeta> intances = fetchAll(service);
                    listener.fire(new Event(intances));
                    VERSIONS.put(service.toPath(), newVer);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
