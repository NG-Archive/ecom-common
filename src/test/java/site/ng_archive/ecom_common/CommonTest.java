package site.ng_archive.ecom_common;


import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import site.ng_archive.ecom_common.auth.UserContext;
import site.ng_archive.ecom_common.auth.token.TokenUtil;
import site.ng_archive.ecom_common.config.AcceptedTest;

@EnableAutoConfiguration(exclude = {
    R2dbcAutoConfiguration.class
})
class CommonTest extends AcceptedTest {

    @Test
    void 토큰인증() {
        UserContext userContext = new UserContext(1L, "USER");
        String token = TokenUtil.getSign(userContext);
        UserContext verified = TokenUtil.verify(token);

        Assertions.assertThat(userContext.id()).isEqualTo(verified.id());
        Assertions.assertThat(userContext.role()).isEqualTo(verified.role());
    }

    @Test
    void 암호화() {
        String plainText = "secret";
        String key = "key";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        encryptor.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
        encryptor.setPassword(key);
        String encrypted = encryptor.encrypt(plainText);
        System.out.println(encrypted);
    }

    @Test
    void 복호화() {
        String encrypted = "cG4Mmc1OJYAlYbBjumbSSD9xV+RcK9ypOkQ7hhgDgn+4uToZ/KeFYsTNlsUFhTxe";
        String key = "key";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        encryptor.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
        encryptor.setPassword(key);
        String plainText = encryptor.decrypt(encrypted);
        System.out.println(plainText);
    }
    @Test
    void mdc_propagation_scenarios() {
        // 1. 같은 스레드 - 동작함
        MDC.put("correlationId", "test-id");
        Mono.fromCallable(() -> MDC.get("correlationId"))
            .as(StepVerifier::create)
            .expectNext("test-id")
            .verifyComplete();

        // 2. 스레드 전환 - MDC 끊김
        Mono.fromCallable(() -> MDC.get("correlationId"))
            .publishOn(Schedulers.boundedElastic())
            .as(StepVerifier::create)
            .expectNext("test-id")
            .verifyComplete();

//        // 3. Reactor Context에 넣으면 - 전파됨 (context-propagation + accessor 필요)
//        Mono.fromCallable(() -> MDC.get("correlationId"))
//            .publishOn(Schedulers.boundedElastic())
//            .contextWrite(ctx -> ctx.put("correlationId", "test-id"))
//            .as(StepVerifier::create)
//            .expectNext("test-id")
//            .verifyComplete();

        MDC.clear();
    }
}
