package site.ng_archive.ecom_common.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorMessageUtil {

    private static final int ERROR_LOG_COUNT = 2;
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
    
    public static void printErrorLog(Throwable e) {
        log.error(buildErrorJson(e));
    }

    public static String buildErrorJson(Throwable e) {
        if (e == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{\"exception\":\"").append(escapeJson(e.toString())).append("\",");
        sb.append("\"stackTrace\":[");
        List<String> frames = filteredFrames(e);
        for (int i = 0; i < frames.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escapeJson(frames.get(i))).append("\"");
        }
        sb.append("]");
        Throwable cause = e.getCause();
        if (cause != null) {
            sb.append(",\"causedBy\":").append(buildErrorJson(cause));
        }
        sb.append("}");
        return sb.toString();
    }

    private static List<String> filteredFrames(Throwable e) {
        List<String> frames = new ArrayList<>();
        int cnt = 0;
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            if ((element.getClassName().startsWith("site.ng_archive") &&
                !element.getClassName().endsWith("Filter"))
                || cnt++ < ERROR_LOG_COUNT
                || i == (stackTrace.length - 1)
            ) {
                frames.add(element.toString());
            }
        }
        return frames;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            sb.append(escapeJsonChar(c));
        }
        return sb.toString();
    }

    private static String escapeJsonChar(char c) {
        return switch (c) {
            case '"' -> "\\\"";
            case '\\' -> "\\\\";
            case '\b' -> "\\b";
            case '\f' -> "\\f";
            case '\n', '\r' -> " ";
            case '\t' -> "\\t";
            default -> c < 0x20 ? String.format("\\u%04x", (int) c) : String.valueOf(c);
        };
    }
}
