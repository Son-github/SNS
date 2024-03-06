package com.sonny.sns.model;

import com.sonny.sns.model.Entity.CommentEntity;
import com.sonny.sns.model.Entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Comment {
    private Integer id;

    private String comment;

    private String userName;

    private Integer postId;

    private Timestamp registerAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    public static Comment fromEntity(CommentEntity entity) { // User를 Dto로 변환
        return new Comment(
                entity.getId(),
                entity.getComment(),
                entity.getUser().getUserName(),
                entity.getPost().getId(),
                entity.getRegisterAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
