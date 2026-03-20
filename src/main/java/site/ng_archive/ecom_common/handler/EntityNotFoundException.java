package site.ng_archive.ecom_common.handler;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String code) {
        super(code);
    }
}
