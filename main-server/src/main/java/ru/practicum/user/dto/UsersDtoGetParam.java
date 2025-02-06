package ru.practicum.user.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UsersDtoGetParam {

    private Integer size = 10;
    private Integer from = 0;
    private List<Long> ids;
}