package com.sonny.sns.service;

import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.fixture.PostEntityFixture;
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

    @Test
    @DisplayName("포스트 수정이 성공한 경우")
    void whenDoPostModifying_givenLoginInformation_thenReturnOk() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity)); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));

    }

    @Test
    @DisplayName("포스트 수정시 포스트가 존재하지 않는 경우")
    void whenDoPostModifying_givenLoginInformation_thenReturnError() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity)); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    @DisplayName("포스트 수정시 권한이 없는 경우")
    void whenDoPostModifying_givenNotAuthorization_thenReturnError() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer)); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

    }

    @Test
    @DisplayName("포스트 삭제가 성공한 경우")
    void whenDoPostDeleting_givenLoginInformation_thenReturnOk() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity)); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.delete(userName, 1));
    }

    @Test
    @DisplayName("포스트 수정시 포스트가 존재하지 않는 경우")
    void whenDoPostDeleting_givenLoginInformation_thenReturnError() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity)); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, 1));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());

    }

    @Test
    @DisplayName("포스트 수정시 권한이 없는 경우")
    void whenDoPostDeleting_givenNotAuthorization_thenReturnError() {
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer)); // Optional은 값이 없는 경우를 표현하기 위해 쓰는 클래스 => Optional은 값이 존재할 수 도 있고, 없을 수도 있다. 이는 NullPointException 예외를 방지 할 수 있고, 코드의 안정성을 높이며 가독성을 향상시킨다.
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.delete(userName, 1));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());

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
        Pageable pageable = mock(Pageable.class);
        UserEntity user = mock(UserEntity.class);

        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());
        Assertions.assertDoesNotThrow(() -> postService.my("", pageable));
    }
}
