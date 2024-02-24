package com.sonny.sns.service;

import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.model.Entity.PostEntity;
import com.sonny.sns.model.Entity.UserEntity;
import com.sonny.sns.repository.PostEntityRepository;
import com.sonny.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    @DisplayName("포스트 작성이 성공한 경우")
    void whenPosting_givenLoginInformation_thenReturnOk() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class))); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));

    }

    @Test
    @DisplayName("포스트 작성시 요청한 유저가 존재하지 않는 경우")
    void whenPosting_givenLoginInformation_thenReturnError() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
}
