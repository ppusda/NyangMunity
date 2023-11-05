package cat.community.NyangMunity.response;

import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoUserAccount {
    private Map<String, String> profile;
    private String email;
}
