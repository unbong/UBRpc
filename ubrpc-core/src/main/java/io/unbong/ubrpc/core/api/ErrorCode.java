package io.unbong.ubrpc.core.api;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-03-30 20:30
 */
public enum ErrorCode {

    SOCKET_TIMEOUT("x", "001", "socket_timeout_exception"),
    NO_SUCH_METHOD("Y", "002", "no_such_method_exception"),
    UNKNOWN("z", "001", "unknown_exception");

    private final String type;
    private final String code;
    private final String errorMessage;

    ErrorCode(String type, String code, String errorMessage){
        this.type = type;
        this.code = code;
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append(type)
                .append(code)
                .append('-')
                .append(errorMessage)
                .toString();
    }
}
