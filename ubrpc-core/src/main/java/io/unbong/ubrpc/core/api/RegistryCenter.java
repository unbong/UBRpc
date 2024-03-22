package io.unbong.ubrpc.core.api;

import io.unbong.ubrpc.core.meta.InstanceMeta;
import io.unbong.ubrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 18:54
 */
public interface RegistryCenter {

    void start(); // p/c
    void stop(); // p/c


    // provider
    /**
     *
     * @param service  服务
     * @param instance 当前节点
     */
    void register(String service, InstanceMeta instance);  // p

    void unregister(String service , InstanceMeta instance); // p

    // consumer
    List<InstanceMeta> fetchAll(String serviceName);  // c

    /**
     * get
     */
    void subscribe(String service, ChangedListener listener);  // c

    class StaticRegistryCenter implements RegistryCenter{

        List<InstanceMeta> _providers;
        public StaticRegistryCenter(List<InstanceMeta> providers){
            _providers = providers;
        }
        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unregister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String serviceName) {
            return _providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {
        }
    }
}
