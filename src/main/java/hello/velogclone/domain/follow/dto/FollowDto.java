package hello.velogclone.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter@Setter
public class FollowDto {
    private String loginId;
    private String profileImageUrl;

    public FollowDto(String loginId, String profileImageUrl) {
        this.loginId = loginId;
        this.profileImageUrl = profileImageUrl;
    }
}
