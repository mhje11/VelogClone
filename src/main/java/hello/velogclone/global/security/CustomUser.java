package hello.velogclone.global.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
public class CustomUser extends User {
    private hello.velogclone.domain.user.entity.User user;

    public CustomUser(hello.velogclone.domain.user.entity.User user) {
        super(user.getLoginId(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }
}
