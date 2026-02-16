package ru.practicum.compilations.params;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicCompilationsParams {
    private Boolean pinned;

    @Builder.Default
    private Integer from = 0;

    @Builder.Default
    private Integer size = 10;
}