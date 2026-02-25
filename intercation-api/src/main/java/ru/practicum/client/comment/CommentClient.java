package ru.practicum.client.comment;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.comment.CommentDto;

import static ru.practicum.util.Constants.*;

@FeignClient(name = NAME_COMMENT_SERVICE, path = PATH_BOX_COMMENTS, fallback = CommentClientFallback.class)
public interface CommentClient {
    @GetMapping(PATH_BOX_COMMENTS_ID)
    CommentDto getCommentById(@PathVariable @NotNull Integer commentId);
}
