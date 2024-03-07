package com.sonny.sns.controller.response;

import lombok.AllArgsConstructor;
import com.sonny.sns.model.User;
import com.sonny.sns.model.UserRole;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UserJoinResponse {
    private Integer id;
    private String userName;

    public static UserJoinResponse fromUser(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUsername()
        );
    }
}
