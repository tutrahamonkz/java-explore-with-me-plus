package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class EventDtoGetParam {
    private Integer size = 10;
    private Integer from = 0;
    private Long userId;
    private Long eventId;
    private List<Long> users; //список id пользователей
    private List<String> states; //список состояний событий
    private List<Long> categories;//список id категорий
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
}
