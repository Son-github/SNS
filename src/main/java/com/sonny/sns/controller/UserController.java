package com.sonny.sns.controller;

import com.sonny.sns.controller.request.UserJoinRequest;
import com.sonny.sns.controller.request.UserLoginRequest;
import com.sonny.sns.controller.response.AlarmResponse;
import com.sonny.sns.controller.response.Response;
import com.sonny.sns.controller.response.UserJoinResponse;
import com.sonny.sns.controller.response.UserLoginResponse;
import com.sonny.sns.model.Alarm;
import lombok.RequiredArgsConstructor;
import com.sonny.sns.model.User;
import com.sonny.sns.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        return Response.success(userService.alarmList(authentication.getName(), pageable).map(AlarmResponse::fromAlarm));
    }
}
