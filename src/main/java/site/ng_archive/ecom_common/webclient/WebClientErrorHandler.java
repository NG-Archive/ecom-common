package site.ng_archive.ecom_common.webclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import site.ng_archive.ecom_common.error.ErrorResponse;
import site.ng_archive.ecom_common.exception.EntityNotFoundException;
import site.ng_archive.ecom_common.exception.ExternalService4xxException;
import site.ng_archive.ecom_common.exception.ExternalService5xxException;
import site.ng_archive.ecom_common.exception.ExternalServiceException;

public class WebClientErrorHandler {
    public static Mono<? extends Throwable> handle(ClientResponse response) {
        return response.bodyToMono(ErrorResponse.class)
            .flatMap(body -> {
                HttpStatusCode status = response.statusCode();

                return switch (status) {
                    case HttpStatus.NOT_FOUND ->
                        Mono.error(new EntityNotFoundException(body.errorCode()));
                    case HttpStatusCode s when s.is4xxClientError() ->
                        Mono.error(new ExternalService4xxException(body.errorCode()));
                    case HttpStatusCode s when s.is5xxServerError() ->
                        Mono.error(new ExternalService5xxException(body.errorCode()));
                    default -> Mono.error(new ExternalServiceException(body.errorCode()));
                };
            });
    }
}

