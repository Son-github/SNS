package com.sonny.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UserLoginRequest {
    private String name;
    private String password;
}
