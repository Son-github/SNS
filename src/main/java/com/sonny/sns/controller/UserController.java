package com.sonny.sns.controller;

import com.sonny.sns.controller.request.UserJoinRequest;
import com.sonny.sns.controller.request.UserLoginRequest;
import com.sonny.sns.controller.response.Response;
import com.sonny.sns.controller.response.UserJoinResponse;
import com.sonny.sns.controller.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import com.sonny.sns.model.User;
import com.sonny.sns.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join (@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getUserName(), request.getPassword());
        UserJoinResponse response = UserJoinResponse.fromUser(user);
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUserName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }
}
