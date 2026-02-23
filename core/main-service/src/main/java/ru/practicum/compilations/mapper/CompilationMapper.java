package ru.practicum.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.config.component.UserClientComponent;
import ru.practicum.dto.user.UserDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;
    private final UserClientComponent userClientComponent;

    public Compilation toCompilation(NewCompilationDto dto, Set<Event> events) {
        return Compilation.builder()
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .title(dto.getTitle())
                .events(events != null ? events : Set.of())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventDtos = compilation.getEvents().stream()
                .map(event -> {
                    UserDto userDto = userClientComponent.getUserById(event.getInitiatorId());

                    return eventMapper.toEventShortDto(event, userDto);
                })
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventDtos)
                .build();
    }

    public static void updateCompilationFromDto(UpdateCompilationRequest dto,
                                                Compilation compilation,
                                                Set<Event> events) {
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (events != null) {
            compilation.setEvents(events);
        }
    }
}
