package com.sonny.sns.controller.response;

import lombok.AllArgsConstructor;
import com.sonny.sns.model.User;
import com.sonny.sns.model.UserRole;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    private Integer id;
    private String userName;
    private UserRole role;

    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUsername(),
                user.getUserRole()
        );
    }
}
