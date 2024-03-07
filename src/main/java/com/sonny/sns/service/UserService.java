package com.sonny.sns.service;

import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.model.Alarm;
import com.sonny.sns.model.User;
import com.sonny.sns.model.Entity.UserEntity;
import com.sonny.sns.repository.AlarmEntityRepository;
import com.sonny.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import com.sonny.sns.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName) throws UsernameNotFoundException {
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName is %s", userName)));
    }

    public String login(String userName, String password) {
        // 회원가입 여부 체크
        User savedUser = loadUserByUserName(userName);
        // 비밀번호 체크
        if(!encoder.matches(password, savedUser.getPassword())){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        return JwtTokenUtils.generateAccessToken(userName, secretKey, expiredTimeMs);
    }

    @Transactional // <= join을 하다가 어떤 exception이 발생을 했을 경우, entity를 save하는 부분이 롤백되면서 유저가 저장되지 않게 만듬.
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("userName is %s", userName));
        });
        // 회원가입 진행 => user를 등록
        UserEntity savedUser = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(savedUser);
    }

    @Transactional
    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
//        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
//                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }
}

