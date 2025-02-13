package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.validation.CreateValidationGroup;
import ru.practicum.validation.UpdateValidationGroup;

@Getter
@Setter
public class CommentDto {

    private Long id;

    private UserShortDto user;

    @NotNull(groups = CreateValidationGroup.class)
    @Positive(groups = CreateValidationGroup.class)
    private Long EventId;

    @NotBlank(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String description;
}
