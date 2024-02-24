package com.sonny.sns.controller.response;

import com.sonny.sns.model.User;
import com.sonny.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {
    private String token;
}
