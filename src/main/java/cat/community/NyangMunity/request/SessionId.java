package cat.community.NyangMunity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
public class SessionId {
    @JsonProperty("SID")
    private String SID;
}
