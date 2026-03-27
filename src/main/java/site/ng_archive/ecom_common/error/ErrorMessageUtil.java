package site.ng_archive.ecom_common.error;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ErrorMessageUtil {

    private final MessageSource ms;
    private static final String EXCEPTION_ERROR_CODE = "error";

    private String getErrorCode(Exception e) {
        String errorCode = e.getMessage();
        try {
            ms.getMessage(errorCode, null, Locale.KOREA);
        } catch (Exception ex) {
            return EXCEPTION_ERROR_CODE;
        }
        return errorCode;
    }

    private String getErrorMessage(String errorCode) {
        try {
            return ms.getMessage(errorCode, null, Locale.KOREA);
        } catch (Exception ex) {
            return ms.getMessage(EXCEPTION_ERROR_CODE, null, Locale.KOREA);
        }
    }

    private String getErrorMessage(String errorCode, Object[] args) {
        try {
            return ms.getMessage(errorCode, args, Locale.KOREA);
        } catch (Exception ex) {
            return ms.getMessage(EXCEPTION_ERROR_CODE, null, Locale.KOREA);
        }
    }

    public ErrorResponse getErrorResult(String errorCode) {
        return new ErrorResponse(errorCode, getErrorMessage(errorCode));
    }

    public ErrorResponse getErrorResult(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }

    public ErrorResponse getErrorResult(Exception e) {
        String code = getErrorCode(e);
        String message = getErrorMessage(code);
        return new ErrorResponse(code, message);
    }

    public ErrorResponse getErrorResult(String errorCode, Object[] arguments) {
        return new ErrorResponse(errorCode, getErrorMessage(errorCode, arguments));
    }
}
