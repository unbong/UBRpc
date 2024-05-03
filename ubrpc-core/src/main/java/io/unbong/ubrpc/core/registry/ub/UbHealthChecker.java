package io.unbong.ubrpc.core.registry.ub;

import com.alibaba.fastjson.JSON;
import io.unbong.ubrpc.core.consumer.HttpInvoker;
import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.meta.ServiceMeta;
import io.unbong.ubrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Check health for registry center
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-05-03 13:43
 */
@Slf4j
public class UbHealthChecker {

    ScheduledExecutorService consumerExecutor;
    ScheduledExecutorService providerExecutor;

    public void start() {
        log.info("----> [UBRegisry] start with server." );
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
    }

    public void stop() {
        log.info("----> [UBRegisry] stoped with server ");
        graceFullShutDown(consumerExecutor);
        graceFullShutDown(providerExecutor);
    }


    public void providerCheck(Callback callback){
        providerExecutor.scheduleWithFixedDelay(()->{
            try{
                callback.call();
            }catch (Exception e)
            {
                log.error(e.getMessage());
            };
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    private void graceFullShutDown(ScheduledExecutorService executorService ){
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            if(!executorService.isTerminated()){
                log.debug("force terminate subscribe operation");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.debug("terminate schedule failed");
        }
    }

    public void consumerCheck(Callback callback){
        consumerExecutor.scheduleWithFixedDelay(()->{
            try{
                callback.call();
            }catch (Exception e){
                log.error(e.getMessage());
            }

        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    public interface Callback{
        void call() throws Exception;
    }
}
