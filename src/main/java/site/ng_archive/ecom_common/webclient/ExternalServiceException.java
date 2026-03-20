package site.ng_archive.ecom_common.webclient;

import lombok.Getter;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String code) {
        super(code);
    }
}
