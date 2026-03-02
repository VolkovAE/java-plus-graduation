package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.CommentServiceExt;

import static ru.practicum.util.Constants.PATH_BOX_COMMENTS_ID;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceExt commentService;

    @GetMapping(PATH_BOX_COMMENTS_ID)
    public CommentDto getCommentById(@PathVariable @NotNull Integer commentId) {
        return commentService.getCommentById(commentId);
    }
}
