package com.huidou.edgeinsight.core.domain.base;

import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.exception.ForbiddenException;
import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNotFoundException(NotFoundException e) {
        return Result.fail(404, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleUnauthorizedException(UnauthorizedException e) {
        return Result.fail(401, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<?> handleForbiddenException(ForbiddenException e) {
        return Result.fail(403, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        return Result.fail(500, "Internal server error: " + e.getMessage());
    }
}
