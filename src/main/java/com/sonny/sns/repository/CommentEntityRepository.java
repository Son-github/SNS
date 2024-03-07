package com.sonny.sns.repository;

import com.sonny.sns.model.Entity.CommentEntity;
import com.sonny.sns.model.Entity.LikeEntity;
import com.sonny.sns.model.Entity.PostEntity;
import com.sonny.sns.model.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {

    Page<CommentEntity> findAllByPost(PostEntity post, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE CommentEntity entity SET entity.removedAt = current_timestamp where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postENtity);
    // jpa에서는 영속성(이걸 하기 위해서는 데이터 베이스에서 데이터를 가져와야함)을 관리하는데 그냥 deleteAll은 데이터들을 다 가져온 다음에 삭제 => 매우 비효율적

}
