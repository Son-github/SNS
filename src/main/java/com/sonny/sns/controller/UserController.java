package com.sonny.sns.controller;

import com.sonny.sns.controller.request.UserJoinRequest;
import com.sonny.sns.controller.request.UserLoginRequest;
import com.sonny.sns.controller.response.AlarmResponse;
import com.sonny.sns.controller.response.Response;
import com.sonny.sns.controller.response.UserJoinResponse;
import com.sonny.sns.controller.response.UserLoginResponse;
import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.model.Alarm;
import com.sonny.sns.util.ClassUtils;
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
        return Response.success(UserJoinResponse.fromUser(userService.join(request.getName(), request.getPassword())));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/me")
    public Response<UserJoinResponse> me(Authentication authentication) {
        return Response.success(UserJoinResponse.fromUser(userService.loadUserByUserName(authentication.getName())));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class);
        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
