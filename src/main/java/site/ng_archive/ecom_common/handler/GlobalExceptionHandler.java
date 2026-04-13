package site.ng_archive.ecom_common.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;
import site.ng_archive.ecom_common.auth.exception.AccessDeniedException;
import site.ng_archive.ecom_common.auth.exception.ForbiddenException;
import site.ng_archive.ecom_common.auth.exception.LoginFailException;
import site.ng_archive.ecom_common.error.ErrorMessageUtil;
import site.ng_archive.ecom_common.error.ErrorResponse;
import site.ng_archive.ecom_common.webclient.ExternalService4xxException;
import site.ng_archive.ecom_common.webclient.ExternalService5xxException;
import site.ng_archive.ecom_common.webclient.ExternalServiceException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorMessageUtil errorMessageUtil;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public ErrorResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> {
                Object[] args = error.getArguments();
                Object[] reversed = null;
                if (args != null && args.length > 1) {
                    Object[] copied = Arrays.copyOfRange(args, 1, args.length);
                    reversed = Arrays.asList(copied).reversed().toArray();
                }
                return errorMessageUtil.getErrorResult(error.getDefaultMessage(), reversed);
            })
            .orElseGet(() -> errorMessageUtil.getErrorResult("error.input.unknown"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestValueException.class)
    public ErrorResponse handleMissingRequestValueException(MissingRequestValueException ex) {
        Object[] args = new Object[]{ex.getLabel(), ex.getName()};
        return errorMessageUtil.getErrorResult("error.missing.request", args);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(UnsupportedOperationException.class)
    public ErrorResponse handleUnsupportedOperationException(UnsupportedOperationException ex) {
        String errorCode = ex.getMessage();
        if (errorCode == null || errorCode.isBlank()) {
            errorCode = "error.unsupported.operation";
        }
        return errorMessageUtil.getErrorResult(errorCode);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        if (StringUtils.hasText(ex.get_code()) && StringUtils.hasText(ex.get_message())) {
            return errorMessageUtil.getErrorResult(ex.get_code(), ex.get_message());
        }
        return errorMessageUtil.getErrorResult(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExternalService4xxException.class)
    public ErrorResponse handleExternalService4xxException(ExternalService4xxException ex) {
        return errorMessageUtil.getErrorResult(ex.get_code(), ex.get_message());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ExternalService5xxException.class)
    public ErrorResponse handleExternalService5xxException(ExternalService5xxException ex) {
        return errorMessageUtil.getErrorResult(ex.get_code(), ex.get_message());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ExternalServiceException.class)
    public ErrorResponse handleExternalServiceException(ExternalServiceException ex) {
        return errorMessageUtil.getErrorResult(ex.get_code(), ex.get_message());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        return errorMessageUtil.getErrorResult(ex);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return errorMessageUtil.getErrorResult(ex);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(LoginFailException.class)
    public ErrorResponse handleLoginFailException(LoginFailException ex) {
        return errorMessageUtil.getErrorResult(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return errorMessageUtil.getErrorResult(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalStateException(IllegalStateException ex) {
        return errorMessageUtil.getErrorResult(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("handleRuntimeException: ", ex);
        String errorCode = "error.runtime";
        return errorMessageUtil.getErrorResult(errorCode);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneralException(Exception ex) {
        log.error("handleGeneralException: ", ex);
        String errorCode = "error.internal.server";
        return errorMessageUtil.getErrorResult(errorCode);
    }
}
