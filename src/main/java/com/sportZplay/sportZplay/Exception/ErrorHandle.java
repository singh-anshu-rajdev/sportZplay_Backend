package com.sportZplay.sportZplay.Exception;

/**
 * ErrorHandle
 */
public interface ErrorHandle {

    /**
     *
     * @return ErrorCode
     */
    Integer getErrorCode();

    /**
     *
     * @return Message
     */
    String getMessage();

}
