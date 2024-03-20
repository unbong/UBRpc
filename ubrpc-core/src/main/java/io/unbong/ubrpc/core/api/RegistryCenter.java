package io.unbong.ubrpc.core.api;

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
    void register(String service, String instance);  // p

    void unregister(String service , String instance); // p

    // consumer
    List<String> fetchAll(String serviceName);  // c

    /**
     * get
     */
    void subscribe(String service, ChangedListener listener);  // c

    class StaticRegistryCenter implements RegistryCenter{

        List<String> _providers;
        public StaticRegistryCenter(List<String> providers){
            _providers = providers;
        }
        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String serviceName) {
            return _providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {
        }
    }
}
