package com.example.attendancesystem.advice;

import com.example.attendancesystem.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

@RestControllerAdvice  // 专门处理 @RestController 返回 JSON 的异常
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理业务异常（如用户名已存在、参数错误等）
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    // 处理权限不足
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return Result.fail("权限不足，无法访问");
    }

    // 处理其他所有未捕获的异常（兜底）
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);  // 打印完整堆栈
        return Result.fail("系统内部错误：" + e.getMessage());
    }
}