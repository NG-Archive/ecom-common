package site.ng_archive.ecom_common.auth.exception;

import lombok.Getter;

public class TokenInvalidException extends RuntimeException {
    public TokenInvalidException(String code) {
        super(code);
    }
}
