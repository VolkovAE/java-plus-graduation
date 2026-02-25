package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.component.EventClientComponent;
import ru.practicum.component.UserClientComponent;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommonCommentDto;
import ru.practicum.dto.comment.DeleteCommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.event.EventState;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.storage.CommentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserClientComponent userRepository;
    private final EventClientComponent eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(Integer userId, Integer eventId, CommonCommentDto newCommentDto) {
        EventFullDto eventFullDto = getEvent(eventId);

        if (!eventFullDto.getState().equals(EventState.PUBLISHED.toString()))
            throw new ConflictException("Нельзя добавить комментарий если событие не опубликовано");

        UserDto userDto = getUser(userId);

        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .text(newCommentDto.getText())
                .eventId(eventFullDto.getId())
                .userId(userDto.getId())
                .build();

        return commentMapper.commentToDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Integer userId, Integer commentId, CommonCommentDto updateCommentDto) {
        Comment comment = getComment(commentId);
        getCommentByUserId(userId, commentId);

        comment.setText(updateCommentDto.getText());

        return commentMapper.commentToDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByUser(Integer userId, Integer commentId) {
        getUser(userId);
        getCommentByUserId(userId, commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getComments(String text, Integer userId, Integer eventId,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {

        if (userId != null) {
            getUser(userId);
        }

        if (eventId != null) {
            getEvent(eventId);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        LocalDateTime start;
        if (rangeStart == null || rangeStart.isBlank()) {
            start = LocalDateTime.of(1900, 1, 1, 0, 0); // Начало времен
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
        }

        LocalDateTime end;
        if (rangeEnd == null || rangeEnd.isBlank()) {
            end = LocalDateTime.now();
        } else {
            end = LocalDateTime.parse(rangeEnd, formatter);
        }

        if (start.isAfter(end)) {
            throw new BadRequestException("Начало времени поиска не может быть позднее его окончания");
        }

        int safeFrom = (from != null) ? Math.max(from, 0) : 0;
        int safeSize = (size != null) ? Math.max(size, 1) : 100;

        PageRequest pageRequest = PageRequest.of(safeFrom / safeSize, safeSize);

        Page<Comment> commentsPage = commentRepository.getComments(
                text, userId, eventId, start, end, pageRequest
        );

        return commentsPage.getContent()
                .stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getEventComments(Integer eventId, Integer from, Integer size) {

        checkEventExists(eventId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Page<Comment> commentPage = commentRepository.findByEventId(eventId, pageable);

        return commentMapper.commentsToDtos(commentPage.getContent());
    }


    @Override
    public List<CommentDto> getCommentsByUserId(Integer userId) {
        getUser(userId);
        List<Comment> commentsList = commentRepository.findByUserId(userId);
        return commentsList.stream()
                .map(commentMapper::commentToDto)
                .toList();
    }

    @Override
    public List<CommentDto> getUserEventComments(Integer userId, Integer eventId) {

        List<Comment> comments = commentRepository.findByUserIdAndEventIdOrderByCreatedDesc(userId, eventId);

        return commentMapper.commentsToDtos(comments);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(DeleteCommentDto deleteCommentsDto) {
        List<Comment> existingComments = commentRepository.findByIdIn(deleteCommentsDto.getCommentsIds());

        List<Integer> existingCommentIds = existingComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        List<Integer> commentIdsNotExist = deleteCommentsDto.getCommentsIds().stream()
                .filter(commentId -> !existingCommentIds.contains(commentId))
                .collect(Collectors.toList());

        if (!commentIdsNotExist.isEmpty()) {
            throw new NotFoundException("Комментарии с id: " + commentIdsNotExist + " не найдены");
        }

        commentRepository.deleteByIdIn(deleteCommentsDto.getCommentsIds());
    }

    @Transactional
    @Override
    public void deleteSingleCommentByAdmin(Integer commentId) {
        DeleteCommentDto dto = new DeleteCommentDto();
        dto.setCommentsIds(List.of(commentId));
        deleteCommentByAdmin(dto);
    }

    private UserDto getUser(Integer userId) {
        return userRepository.getUserById(userId);
    }

    private EventFullDto getEvent(Integer eventId) {
        return eventRepository.getEventById(eventId);
    }

    private Comment getCommentByUserId(Integer userId, Integer commentId) {
        return commentRepository.findByUserIdAndId(userId, commentId).orElseThrow(
                () -> new ConflictException(
                        "Пользователю id: " + userId + " не принадлежит комментарий с id: " + commentId)
        );
    }

    private Comment getComment(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментария с id: " + commentId + " не существует")
        );
    }

    private void checkEventExists(Integer eventId) {
        getEvent(eventId);
    }
}
