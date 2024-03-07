package com.sonny.sns.service;

import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.fixture.TestInfoFixture;
import com.sonny.sns.fixture.UserEntityFixture;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        // mocking
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty()); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));
        SnsApplicationException exception = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("포스트 작성시 요청한 유저가 존재하지 않는 경우")
    void whenPosting_givenLoginInformation_thenReturnError() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () ->
                postService.create(fixture.getUserName(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("포스트 수정시 포스트가 존재하지 않는 경우")
    void whenDoPostModifying_givenLoginInformation_thenReturnError() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.empty()); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () ->
                postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    @DisplayName("포스트 수정시 권한이 없는 경우")
    void whenDoPostModifying_givenNotAuthorization_thenReturnError() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty()); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () ->
                postService.modify(fixture.getUserId(), fixture.getPostId(), fixture.getTitle(), fixture.getBody()));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());

    }

    @Test
    @DisplayName("포스트 수정시 포스트가 존재하지 않는 경우")
    void whenDoPostDeleting_givenLoginInformation_thenReturnError() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        when(userEntityRepository.findByUserName(fixture.getUserName())).thenReturn(Optional.empty()); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mock(PostEntity.class)));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () ->
                postService.delete(fixture.getUserId(), fixture.getPostId()));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    @DisplayName("피드목록요청이 성공한 경우")
    void whenDoFeedRequest_givenLoginInformation_thenReturnOk() {
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    @DisplayName("내 피드목록요청이 성공한 경우")
    void whenDoMyFeedRequest_givenLoginInformation_thenReturnOk() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAllByUserId(any(), pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.my(fixture.getUserId(), pageable));
    }
}
