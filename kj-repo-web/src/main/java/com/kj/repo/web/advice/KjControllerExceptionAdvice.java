package com.kj.repo.web.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kj.repo.base.model.ResultInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kj
 */
@Slf4j
@ControllerAdvice
public class KjControllerExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultInfo<Void> handleException(HttpServletRequest request, Throwable t) {
        log.info("", t);
        return ResultInfo.fail("exception");
    }

}
