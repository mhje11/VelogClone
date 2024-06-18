package hello.velogclone.user.entity;

import lombok.Getter;
@Getter
public enum Role {
    ROLE_USER("member"), ROLE_ADMIN("Admin");
    String description;

    Role(String description) {
        this.description = description;
    }
}
