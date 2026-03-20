package site.ng_archive.ecom_common.exception;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {

    @Getter
    private String code;

    public EntityNotFoundException(String code) {
        super(code);
        this.code = code;
    }
}
