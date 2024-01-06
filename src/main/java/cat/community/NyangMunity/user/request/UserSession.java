package cat.community.NyangMunity.user.request;

public class UserSession {
    public final long id;
    public final String token;

    public UserSession(long id, String token) {
        this.id = id;
        this.token = token;
    }
}
