package ru.practicum.comment.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "eventId", source = "event.id")
    CommentDto toDto(Comment comment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    List<CommentDto> toDtos(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    Comment toEntity(CommentDto commentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDto(CommentDto commentDto,@MappingTarget Comment comment);
}
