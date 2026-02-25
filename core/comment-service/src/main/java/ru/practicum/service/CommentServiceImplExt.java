package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.storage.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImplExt implements CommentServiceExt {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto getCommentById(Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ConflictException(
                        "Комментария с id: " + commentId + " не существует."));

        return commentMapper.commentToDto(comment);
    }
}
