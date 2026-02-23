package ru.practicum.events.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.comment.dto.DeleteCommentDto;
import ru.practicum.events.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable @Positive Integer commentId) {
        commentService.deleteSingleCommentByAdmin(commentId);
    }

    @DeleteMapping("/admin/comments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentsByAdmin(@Valid @RequestBody DeleteCommentDto deleteCommentDto) {
        commentService.deleteCommentByAdmin(deleteCommentDto);
    }
}
