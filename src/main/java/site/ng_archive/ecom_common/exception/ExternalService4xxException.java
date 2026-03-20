package site.ng_archive.ecom_common.exception;

import lombok.Getter;

public class ExternalService4xxException extends RuntimeException {

    @Getter
    private String code;

    public ExternalService4xxException(String code) {
        super(code);
        this.code = code;
    }
}
