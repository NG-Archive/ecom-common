package site.ng_archive.ecom_common.webclient;

import lombok.Getter;

public class ExternalService5xxException extends RuntimeException {
    public ExternalService5xxException(String code) {
        super(code);
    }
}
