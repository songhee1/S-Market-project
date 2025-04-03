package com.flab.s_market.common.exception;

import com.flab.s_market.common.entity.ApiResponse;
import com.google.common.base.Joiner;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // error response를 정확히 어떻게 바꿔야할지 고민중
    @Order(1)
    @ExceptionHandler(value={CustomException.class})
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e){
        Map<String, Object> parameters = e.getParameters();

        String join = Joiner.on(",").withKeyValueSeparator("=").join(parameters);
        log.error(join);
        log.error("CustomException occured : " + e.getErrorCode().getInternalMessage());

        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ApiResponse.createFail(e));
    }
    @Order(2)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {

        String errorMessage = ExceptionUtils.getFullStackTrace(e);
        CustomException customException = new CustomException(ErrorCode.NOT_MATCH_TERM_VERSION, Map.of("exception", errorMessage), log::info);

        log.error("ConstraintViolationException occured : " + customException.getErrorCode().getInternalMessage());

        return new ResponseEntity<>(ApiResponse.createFailWithErrorMessage(customException.getErrorCode().getExternalMessage()), customException.getErrorCode()
            .getHttpStatus());
    }

    @Order(3)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {

        String errorMessage = ExceptionUtils.getFullStackTrace(e);
        CustomException customException = new CustomException(ErrorCode.REQUIRE_PARAMETER, Map.of("exception", errorMessage), log::info);

        log.error("MissingServletRequestParameterException occured : " + customException.getErrorCode().getInternalMessage());

        return new ResponseEntity<>(ApiResponse.createFailWithErrorMessage(customException.getErrorCode().getExternalMessage()), customException.getErrorCode()
            .getHttpStatus());
    }

    @Order(99)
    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ApiResponse<?>> handleException(Exception e){

        String errorMessage = ExceptionUtils.getFullStackTrace(e);
        CustomException customException = new CustomException(ErrorCode.ERROR, Map.of("exception", errorMessage), log::info);

        log.error("Exception occured : " + customException.getErrorCode().getInternalMessage());
        log.error("Exception parameter : " + customException.getParameters());
        log.error("Exception logInfo : " + errorMessage);
        // 9999
        return new ResponseEntity<>(ApiResponse.createFailWithErrorMessage(customException.getErrorCode().getExternalMessage()), customException.getErrorCode()
            .getHttpStatus());
    }
}
