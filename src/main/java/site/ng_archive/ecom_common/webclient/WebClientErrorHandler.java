package site.ng_archive.ecom_common.webclient;

import site.ng_archive.ecom_common.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import site.ng_archive.ecom_common.handler.EntityNotFoundException;

public class WebClientErrorHandler {

    private static final String EMPTY_RESPONSE_ERROR = "error.response.empty";
    private static final String INVALID_RESPONSE_ERROR = "error.response.invalid";

    public static Mono<? extends Throwable> handle(ClientResponse response) {
        return response.bodyToMono(ErrorResponse.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalStateException(EMPTY_RESPONSE_ERROR))))
                .onErrorMap(ex -> isExpectedEmptyResponseError(ex)
                    ? ex : new IllegalStateException(INVALID_RESPONSE_ERROR, ex))
                .flatMap(body -> Mono.error(mapException(response.statusCode(), body)));
    }

    private static boolean isExpectedEmptyResponseError(Throwable ex) {
        return ex instanceof IllegalStateException
                && EMPTY_RESPONSE_ERROR.equals(ex.getMessage());
    }

    private static RuntimeException mapException(HttpStatusCode status, ErrorResponse body) {
        return switch (status) {
            case HttpStatus httpStatus when httpStatus == HttpStatus.NOT_FOUND ->
                    new EntityNotFoundException(body.errorCode(), body.message());
            case HttpStatusCode s when s.is4xxClientError() ->
                    new ExternalService4xxException(body.errorCode(), body.message());
            case HttpStatusCode s when s.is5xxServerError() ->
                    new ExternalService5xxException(body.errorCode(), body.message());
            default ->
                    new ExternalServiceException(body.errorCode(), body.message());
        };
    }
}

