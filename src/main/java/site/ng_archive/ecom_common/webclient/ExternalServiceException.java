package site.ng_archive.ecom_common.webclient;

import lombok.Getter;

public class ExternalServiceException extends RuntimeException {
    @Getter
    String _code;
    @Getter
    String _message;
    public ExternalServiceException(String code, String message) {
        super(code);
        this._code = code;
        this._message = message;
    }
}
