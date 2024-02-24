package com.sonny.sns.service;

import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.model.Entity.PostEntity;
import com.sonny.sns.model.Entity.UserEntity;
import com.sonny.sns.repository.PostEntityRepository;
import com.sonny.sns.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor // Repository가 필요할 때 쓰는 어노테이션
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional // 데이터베이스와 상호작용을 할 때, 트랜잭션을 적용하면 데이터 추가, 갱신, 삭제 등으로 이루어진 작업을 처리하던 중 오류가 난다면 모든 작업 상태를 원래대로 되돌린다.
    public void create(String title, String body, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }
}
