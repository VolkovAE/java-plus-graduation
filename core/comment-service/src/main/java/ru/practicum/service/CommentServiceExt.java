package ru.practicum.service;

import ru.practicum.dto.comment.CommentDto;

public interface CommentServiceExt {
    CommentDto getCommentById(Integer commentId);
}
