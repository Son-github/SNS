package com.sonny.sns.model.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.sonny.sns.model.AlarmArgs;
import com.sonny.sns.model.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"alarm\"", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Data
@SQLDelete(sql = "UPDATE \"alarm\" SET removed_at = NOW() where id=?")
@Where(clause = "removed_at is NULL")
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 알람을 받은 사람
    // JPA n+1 문제가 생길 수 있기 때문에 수정
    @ManyToOne(fetch = FetchType.LAZY) // n+1 문제를 해결해주지는 않음 -> 불필요한 query를 없애줄 뿐, n+1은 발생한다. -> 해결하기 위해서는 Repository에 @Query를 써서 새로운 쿼리를 짜서 해결.
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Type(JsonBinaryType.class) // Springboot3 이상 버전은 이렇게 써야함.
    @Column(columnDefinition = "jsonb") // json은 json 그대로 저장, jsonb는 한번 압축해서 저장 => jsonb만 인덱스를 걸 수 있다.
    private AlarmArgs args;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "removed_at")
    private Timestamp removedAt;

    @PrePersist // 자동으로 저장되도록
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static AlarmEntity of(AlarmType alarmType, AlarmArgs args, UserEntity user){
        AlarmEntity entity = new AlarmEntity();
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        entity.setUser(user);
        return entity;
    }
}
