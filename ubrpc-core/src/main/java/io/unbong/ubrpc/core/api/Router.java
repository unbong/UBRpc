package io.unbong.ubrpc.core.api;

import java.util.List;

public interface Router<T> {

    List<T> route(List<T> provider);

    /**
     * 路由的默认实现
     *
     */
    Router Default  = provider -> provider;
}
