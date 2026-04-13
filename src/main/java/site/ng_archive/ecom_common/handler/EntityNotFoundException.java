package site.ng_archive.ecom_common.handler;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {
    @Getter
    String _code;
    @Getter
    String _message;

    public EntityNotFoundException(String code) {
        super(code);
    }

    public EntityNotFoundException(String code, String message) {
        super(code);
        this._code = code;
        this._message = message;
    }
}
