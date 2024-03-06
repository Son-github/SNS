package com.sonny.sns.controller.response;

import com.sonny.sns.model.Alarm;
import com.sonny.sns.model.AlarmArgs;
import com.sonny.sns.model.AlarmType;
import com.sonny.sns.model.Entity.AlarmEntity;
import com.sonny.sns.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
public class AlarmResponse {
    private Integer id;
    private AlarmType alarmType;
    private AlarmArgs alarmArgs;
    private String text;
    private Timestamp registeredAt;
    private Timestamp upgradeAt;
    private Timestamp deletedAt;

    public static AlarmResponse fromAlarm(Alarm alarm){
        return new AlarmResponse(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getArgs(),
                alarm.getAlarmType().getAlarmText(),
                alarm.getRegisteredAt(),
                alarm.getUpgradedAt(),
                alarm.getDeletedAt()
        );
    }
}
