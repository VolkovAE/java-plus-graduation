package ru.practicum.events.params;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicEventParams {
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;

    @Builder.Default
    private String sort = "EVENT_DATE";

    @Builder.Default
    @Min(0)
    private Integer from = 0;

    @Builder.Default
    @Min(1)
    @Max(1000)
    private Integer size = 10;
}