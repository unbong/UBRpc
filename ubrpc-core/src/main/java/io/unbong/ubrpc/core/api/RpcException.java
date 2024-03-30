package io.unbong.ubrpc.core.api;

import lombok.Data;

/**
 *  Rpc异常
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-30 13:47
 */
@Data
public class RpcException extends RuntimeException {

    // x-> 技术类异常
    // y-> 业务类异常
    // z-> unknown 暂时搞不清楚，再归类到x或y
//    public static final String SocketTimeoutEx = "x001"+"-"+ "http_invoke_timeout.";
//    public static final String NoSuchMethodEx = "x002"+"-"+ "method_no_exist.";
//    public static final String Unknown = "z002"+"-"+ "unknown.";


    private String errorCode;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(Throwable cause, ErrorCode errorCode){
        super(cause);
        this.errorCode = errorCode.toString();
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
