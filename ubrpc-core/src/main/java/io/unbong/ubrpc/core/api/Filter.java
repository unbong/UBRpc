package io.unbong.ubrpc.core.api;


/**
 * 过滤器
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-17 16:10
 */
public interface Filter {

    Object preFilter(RpcRequest request);

    /**
     *
     * @param response
     * @return
     */
    Object postFilter(RpcRequest request, RpcResponse response, Object result);

    Filter next();


    Filter Default = new Filter() {
        @Override
        public RpcResponse preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }

        @Override
        public Filter next() {
            return null;
        }
    };
}
