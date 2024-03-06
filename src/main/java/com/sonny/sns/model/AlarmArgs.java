package com.sonny.sns.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
public class AlarmArgs {
    // 알람을 발생시킨 사람
    private Integer fromUserId;
    private Integer targetId;

    public AlarmArgs() {
    } // JPA에서 요구하는 기본 생성자가 클래스에 존재하게 되어 문제를 해결할 수 있음.
}

// comment : 00시가 새 코멘트를 자가성했습니다. -> postId, commentId
// 00외 2명이 새 코멘트를 작성했습니다. -> commentId, commentId
