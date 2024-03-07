package com.sonny.sns.service;

import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.fixture.TestInfoFixture;
import com.sonny.sns.fixture.UserEntityFixture;
import com.sonny.sns.model.Entity.UserEntity;
import com.sonny.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {


    /*
    Mock: 테스트를 위해 실체 객체를 사용하는 것처럼 테스트를 위해 만든 모형으로 가짜 객체를 의미
    Mocking: Mock을 이용해서 테스트하는 과정

    @WebMvcTest: 컨트롤러를 test할 때 사용하는 어노테이션
    (1)@Controller, @ControllerAdvice 등 사용 가능(@Service, @Repository는 사용 불가능)
    (2)@MockBean을 사용하여 컨트롤러의 협력체들 생성
    (3)주로 간단한 테스트를 사용

    @AutoConfigureMockMvc: 컨트롤러를 test할 때 사용하는 어노테이션
    (1)컨트롤러뿐만 아니라 @Service, @Repository도 사용 가능
    (2)@AutoConfigureMockMvc와 @SpringBootTest를 결합하여 사용
    (3)주로 MockMvc를 보다 세밀하게 제어할 경우 사용

    @MockBean: 가짜 객체를 가져온 형태로 spring-boot-test의 Mockito와 결합하여 실제 로그인한 사용자 또는 인가되지 않은 사용자 등의 테스트가 가능
    @Autowired: 실제 Bean을 찾아 주입해주는 용도로 구현된 내용을 사용

    Spring Container
    => 자바 어플리케이션은 어플리케이션 동작을 제공하는 객체들로 이루어져 있다.
    이때, 객체들은 독립적으로 동작하는 것보다 서로 상호작용하여 동작하는 경우가 많다.
    Spring에서는 Spring Container가 객체들을 생성하고 객체끼리 의존성을 주입하는 역할을 한다.
    그리고, Spring Container가 생성한 객체들을 Bean이라고 한다.
    즉, Bean은 스프링에서 사용하는 어플리케이션 객체라고 이해할 수 있다.

    Bean 생성방법
    (1) xml활용
    (2) 어노테이션 활용
    */
    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원가입이 정상적으로 동작하는 경우")
    void whenDoSignUp_givenRightData_thenReturnOk() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));
        when(bCryptPasswordEncoder.encode(fixture.getPassword())).thenReturn("password_encrypt");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), "password_encrypt")));

        Assertions.assertDoesNotThrow(() -> userService.join(fixture.getUserName(), fixture.getPassword()));
    }

    @Disabled
    @Test
    @DisplayName("회원가입시 userName으로 회원가입한 유저가 이미 있는 경우")
    void whenDoSignUp_giveAlreadyHaveUserName_thenReturnError() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.of(UserEntityFixture.get(fixture.getUserName(), fixture.getPassword())));

        SnsApplicationException exception = Assertions.assertThrows(SnsApplicationException.class,
                () -> userService.join(fixture.getUserName(), fixture.getPassword()));

        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인이 정상적으로 동작하는 경우")
    void whenDoLogin_givenRightData_thenReturnOk() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> userService.login(fixture.getUserName(), fixture.getPassword()));
    }

    @Test
    @DisplayName("로그인시 userName이 없는 경우")
    void whenDoLogin_giveDoNotHaveUserName_thenReturnError() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        SnsApplicationException exception = Assertions.assertThrows(SnsApplicationException.class
                , () -> userService.login(fixture.getUserName(), fixture.getPassword()));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}
