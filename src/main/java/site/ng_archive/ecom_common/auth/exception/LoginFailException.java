package site.ng_archive.ecom_common.auth.exception;

import lombok.Getter;

public class LoginFailException extends RuntimeException {
    public LoginFailException(String code) {
        super(code);
    }
}
