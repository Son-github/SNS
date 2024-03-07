package com.sonny.sns.repository;

import com.sonny.sns.model.Entity.LikeEntity;
import com.sonny.sns.model.Entity.PostEntity;
import com.sonny.sns.model.Entity.UserEntity;
import com.sonny.sns.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    // select * from "like" where post_id = 2

    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post = :post")
    Integer countByPost(@Param("post") PostEntity post);

    List<LikeEntity> findAllByPost(PostEntity post);

    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity set entity.removedAt = current_timestamp where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
