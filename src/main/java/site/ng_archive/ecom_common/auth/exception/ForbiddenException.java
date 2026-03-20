package site.ng_archive.ecom_common.auth.exception;

import lombok.Getter;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String code) {
        super(code);
    }
}
