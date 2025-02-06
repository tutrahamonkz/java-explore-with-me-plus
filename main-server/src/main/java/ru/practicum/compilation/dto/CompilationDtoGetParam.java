package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
public class CompilationDtoGetParam {

    private Boolean pinned;
    @PositiveOrZero
    private Integer from = 0;
    @Positive
    private Integer size = 10;
}