package org.dinky.exception;

/**
 * @author ZackYoung
 * @version 1.0
 * @since 2022/9/2
 */
public class SplitTableException extends RuntimeException {
    public SplitTableException(String message, Throwable cause) {
        super(message, cause);
    }

    public SplitTableException(String message) {
        super(message);
    }
}
