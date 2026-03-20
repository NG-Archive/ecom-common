package site.ng_archive.ecom_common.auth;

public record UserContext(Long id, String role) {
    public static UserContext of(Long id, String role) {
        return new UserContext(id, role);
    }
}
