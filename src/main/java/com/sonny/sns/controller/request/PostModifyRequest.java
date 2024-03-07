package com.sonny.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class PostModifyRequest {
    private String title;
    private String body;
}
