package io.unbong.ubrpc.core.registry;

import io.unbong.ubrpc.core.api.RegistryCenter;

import java.util.List;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 19:42
 */
public class ZKRegistryCenter implements RegistryCenter {
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
        return null;
    }

    @Override
    public void subscribe() {

    }
}
