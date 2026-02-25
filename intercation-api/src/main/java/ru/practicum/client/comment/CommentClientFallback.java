package ru.practicum.client.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.comment.CommentDto;

@Component
@Slf4j
public class CommentClientFallback implements CommentClient {
    @Override
    public CommentDto getCommentById(Integer commentId) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setText(null);
        commentDto.setUserId(null);
        commentDto.setEventId(null);

        return commentDto;
    }
}
