package com.sonny.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonny.sns.controller.request.*;
import com.sonny.sns.exception.ErrorCode;
import com.sonny.sns.exception.SnsApplicationException;
import com.sonny.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 작성")
    void whenDoPosting_givenLogin_thenReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostWriteRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser // 로그인 되지 않은 경우를 표시할 수 있음
    @DisplayName("포스트 작성시 로그인 하지 않은 경우")
    void whenDoPosting_givenNotLogin_thenReturnError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostWriteRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 수정시 로그인 하지 않은 경우")
    void whenDoPostModifying_givenNotLogin_thenReturnError() throws Exception {
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostWriteRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 수정시 본인이 작성한 글이 아니라면 에러 발생")
    void whenDoPostModifying_givenNotMaker_thenReturnError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostWriteRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 수정시 수정하려는 글이 없는 경우 에러 발생")
    void whenDoPostModifying_givenNoPosting_thenReturnError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostWriteRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 수정시 데이터베이스 에러 발생시 에러 발생")
    void whenDoPostModifying_givenDataBaseError_thenReturnError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.DATABASE_ERROR)).when(postService).modify(any(), eq(1), eq("title"), eq("body"));
        mockMvc.perform(put("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostWriteRequest("title", "body")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 삭제시 로그인 하지 않은 경우 에러 발생")
    void whenDoPostDeleting_givenNotAnything_thenReturnError() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 삭제시 작성자와 삭제 요청자가 다를 경우")
    void whenDoPostDeleting_givenWrongLogin_thenReturnError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 삭제시 삭제하려는 포스트가 존재하지 않을 경우")
    void whenDoPostDeleting_givenNoPosts_thenReturnOk() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any()); // delete가 void기 때문에 when보다는 doThrow를 쓴다.
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("포스트 삭제시 데이터베이스 에러가 발생할 경우")
    void whenDoPostDeleting_givenDataBaseError_thenReturnOk() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.DATABASE_ERROR)).when(postService).delete(any(), any()); // delete가 void기 때문에 when보다는 doThrow를 쓴다.
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("피드 목록")
    void whenCallFeed_givenLogin_thenReturnFeed() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("피드 목록 요청시 로그인 하지 않은 경우")
    void whenCallFeed_givenDoNotLogin_thenReturnError() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("자신의 피드 목록")
    void whenCallMyFeed_givenLogin_thenReturnMyFeed() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("자신의 피드 목록 요청시 로그인 하지 않은 경우")
    void whenCallMyFeed_givenDoNotLogin_thenReturnError() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("좋아요 기능")
    void whenClickLikeButton_given_thenReturnOk() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("좋아요 버튼 클릭시 로그인 하지 않은 경우")
    void whenClickLikeButton_givenDoNotLogin_thenReturnError() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("좋아요 버튼 클릭시 게시물이 없는 경우")
    void whenClickLikeButton_givenDontHaveAnyFeed_thenReturnError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser // 로그인이 된 경우
    @DisplayName("댓글 기능")
    void whenDoingComment_given_thenReturnOk() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment")))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("댓글 작성 시 로그인 하지 않은 경우")
    void whenDoingComment_givenDoNotLogin_thenReturnError() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment")))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 작성 시 게시물이 없는 경우")
    void whenDoingComment_givenDontHaveAnyPost_thenReturnError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).comment(any(), any(), any());

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCommentRequest("comment")))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
}
