package site.ng_archive.ecom_common.exception;

import lombok.Getter;

public class ExternalService5xxException extends RuntimeException {

    @Getter
    private String code;

    public ExternalService5xxException(String code) {
        super(code);
        this.code = code;
    }
}
