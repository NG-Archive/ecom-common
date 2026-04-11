package site.ng_archive.ecom_common.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(ROLES.USER),
    SELLER(ROLES.SELLER),
    ADMIN(ROLES.ADMIN);

    private final String role;

    public String toString() {
        return this.name();
    }

    public static class ROLES {
        public static final String USER = "USER";
        public static final String SELLER = "SELLER";
        public static final String ADMIN = "ADMIN";
    }
}
