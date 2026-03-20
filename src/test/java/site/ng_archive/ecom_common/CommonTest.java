package site.ng_archive.ecom_common;


import org.assertj.core.api.Assertions;
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

}
