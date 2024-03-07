package com.sonny.sns.repository;

import com.sonny.sns.model.Entity.PostEntity;
import com.sonny.sns.model.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    public Page<PostEntity> findAllByUserId(Integer userId, Pageable pageable);

}
