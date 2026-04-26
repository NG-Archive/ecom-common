package site.ng_archive.ecom_common;


import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import site.ng_archive.ecom_common.auth.UserContext;
import site.ng_archive.ecom_common.auth.token.TokenUtil;
import site.ng_archive.ecom_common.config.AcceptedTest;

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
        String plainText = "ecom-order-mysql-password";
        String key = "ecom-order-prod";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        encryptor.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
        encryptor.setPassword(key);
        String encrypted = encryptor.encrypt(plainText);
        System.out.println(encrypted);
    }

    @Test
    void 복호화() {
        String encrypted = "kL9ZaBsxJ/KBfTjj0WnpVygrmxjVF0iuJ0Y6jfkzQSYlQcKTywtEelhVmHEbZrh/Z5a06OJ4bjTaPVRMLImVwA==";
        String key = "ecom-order-prod";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        encryptor.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
        encryptor.setPassword(key);
        String plainText = encryptor.decrypt(encrypted);
        System.out.println(plainText);
    }
}
