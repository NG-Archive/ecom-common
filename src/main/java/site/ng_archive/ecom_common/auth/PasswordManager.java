package site.ng_archive.ecom_common.auth;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordManager {

    public static String encrypt(String plainPw) {
        return BCrypt.hashpw(plainPw, BCrypt.gensalt());
    }

    public static Boolean check(String plainPw, String encryptedPw) {
        return BCrypt.checkpw(plainPw, encryptedPw);
    }

}
