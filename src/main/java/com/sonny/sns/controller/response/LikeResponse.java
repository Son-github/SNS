package com.sonny.sns.controller.response;

import com.sonny.sns.model.Like;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class LikeResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp removedAt;

    public static LikeResponse fromLike(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getUserId(),
                like.getUserName(),
                like.getPostId(),
                like.getRegisteredAt(),
                like.getUpdatedAt(),
                like.getRemovedAt()
        );
    }
}
