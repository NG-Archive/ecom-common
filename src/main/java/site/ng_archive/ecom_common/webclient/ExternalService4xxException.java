package site.ng_archive.ecom_common.webclient;

import lombok.Getter;

public class ExternalService4xxException extends RuntimeException {
    public ExternalService4xxException(String code) {
        super(code);
    }
}
