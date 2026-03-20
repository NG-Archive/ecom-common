package site.ng_archive.ecom_common.auth.exception;

import lombok.Getter;

public class AccessDeniedException extends RuntimeException {
    @Getter
    private String code;

    public AccessDeniedException(String code) {
        super(code);
        this.code = code;
    }
}
