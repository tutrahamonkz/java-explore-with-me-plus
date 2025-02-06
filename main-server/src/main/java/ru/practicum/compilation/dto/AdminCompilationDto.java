package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.validation.CreateValidationGroup;
import ru.practicum.validation.UpdateValidationGroup;

import java.util.List;

@Getter
@Setter
public class AdminCompilationDto {

    private List<Long> events;

    private Boolean pinned = false;

    @NotBlank(groups = CreateValidationGroup.class)
    @NotEmpty(groups = UpdateValidationGroup.class)
    @Size(max = 50)
    private String title;
}