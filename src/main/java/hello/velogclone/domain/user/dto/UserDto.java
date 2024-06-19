package hello.velogclone.domain.user.dto;

import hello.velogclone.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String role;

    public UserDto(String loginId, String password, String email, String name, String role) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
    }
    public static UserDto of(User user) {
        UserDto userDto = new UserDto();
        userDto.setPassword(user.getPassword());
        userDto.setLoginId(user.getLoginId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().getDescription());
        return userDto;
    }
}
