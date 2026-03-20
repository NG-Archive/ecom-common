package site.ng_archive.ecom_common.auth.exception;

import lombok.Getter;

public class ForbiddenException extends RuntimeException {
    @Getter
    private String code;

    public ForbiddenException(String code) {
        super(code);
        this.code = code;
    }
}
