package site.ng_archive.ecom_common.exception;

import lombok.Getter;

public class ExternalServiceException extends RuntimeException {

    @Getter
    private String code;

    public ExternalServiceException(String code) {
        super(code);
        this.code = code;
    }
}
