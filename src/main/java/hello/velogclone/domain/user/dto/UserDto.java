package hello.velogclone.domain.user.dto;

import hello.velogclone.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String role;
    private Boolean receiveEmail;
    private Boolean commentNotification;
    private String profileImageUrl;


    public static UserDto of(User user) {
        UserDto userDto = new UserDto();
        userDto.setPassword(user.getPassword());
        userDto.setLoginId(user.getLoginId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().getDescription());
        userDto.setReceiveEmail(user.getReceiveEmail());
        userDto.setCommentNotification(user.getCommentNotification());

        return userDto;
    }
}
