package site.ng_archive.ecom_common.auth.exception;

import lombok.Getter;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String code) {
        super(code);
    }
}
