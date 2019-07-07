package com.tamboot.web.core;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.common.web.ApiResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class TambootResponseEntityExceptionHandler {
    private final Log logger = LogFactory.getLog(getClass());

    @ExceptionHandler
    public ResponseEntity<TambootResponse> handleException(Exception except) {
        if (except instanceof BusinessException) {
           return this.doHandleBusinessException((BusinessException) except);
        } else if (except instanceof BindException) {
            return this.doHandleBindException((BindException) except);
        } else if (except instanceof MethodArgumentNotValidException) {
            return this.doMethodArgumentNotValidException((MethodArgumentNotValidException) except);
        } else {
            logger.error(ExceptionUtil.stackTraceText(except));
            return ResponseEntity.ok(TambootResponse.exception());
        }
    }

    private ResponseEntity<TambootResponse> doHandleBusinessException(BusinessException except) {
        String code = except.getCode();
        String msg = except.getMessage();
        if (StringUtils.isEmpty(code)) {
            code = ApiResponseType.FAIL.getCode();
        }
        if (StringUtils.isEmpty(msg)) {
            msg = ApiResponseType.FAIL.getMsg();
        }
        return ResponseEntity.ok(new TambootResponse(code, msg));
    }

    private ResponseEntity<TambootResponse> doHandleBindException(BindException except) {
        String msg = ApiResponseType.FAIL.getMsg();

        List<ObjectError> allErrors = except.getAllErrors();
        if (!CollectionUtils.isEmpty(allErrors)) {
            for (ObjectError error : allErrors) {
                if (!StringUtils.isEmpty(error.getDefaultMessage())) {
                    msg = error.getDefaultMessage();
                    break;
                }
            }
        }

        return ResponseEntity.ok(TambootResponse.fail(msg));
    }

    private ResponseEntity<TambootResponse> doMethodArgumentNotValidException(MethodArgumentNotValidException except) {
        String msg = ApiResponseType.FAIL.getMsg();

        if (except.getBindingResult() == null) {
            return ResponseEntity.ok(TambootResponse.fail(msg));
        }

        List<ObjectError> allErrors = except.getBindingResult().getAllErrors();
        if (!CollectionUtils.isEmpty(allErrors)) {
            for (ObjectError error : allErrors) {
                if (!StringUtils.isEmpty(error.getDefaultMessage())) {
                    msg = error.getDefaultMessage();
                    break;
                }
            }
        }

        return ResponseEntity.ok(TambootResponse.fail(msg));
    }
}
