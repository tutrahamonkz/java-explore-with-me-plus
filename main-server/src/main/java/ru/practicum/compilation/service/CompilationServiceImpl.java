package ru.practicum.compilation.service;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.AdminCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoGetParam;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.QCompilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    /**
     * Возвращает подборку событий.
     *
     * @param id идентификатор подборки событий
     * @return DTO подборки событий
     */
    @Override
    public CompilationDto get(Long id) {
        log.info("Получение подборки событий с id: {}", id);
        return CompilationMapper.INSTANCE.toDto(getById(id));
    }

    /**
     * Возвращает список подборок событий с учетом параметров фильтрации.
     *
     * @param param объект с параметрами фильтрации
     * @return список DTO подборок событий
     */
    @Override
    public List<CompilationDto> getAll(CompilationDtoGetParam param) {
        log.info("Получение списка подборок событий с параметрами: from={}, size={}",
                param.getFrom(), param.getSize());
        QCompilation compilation = QCompilation.compilation;
        Predicate predicate = compilationPredicate(param, compilation);
        PageRequest pageRequest = PageRequest.of(param.getFrom(), param.getSize());

        List<Compilation> compilations;

        // Если предикат равен null, возвращаем все подборки событий с пагинацией, иначе по предикату с пагинацией
        if (predicate == null) {
            log.info("Запрос всех подборок событий с пагинацией: from={}, size={}", param.getFrom(), param.getSize());
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            log.info("Запрос подборок событий по предикату с пагинацией: from={}, size={}, predicate={}",
                    param.getFrom(), param.getSize(), predicate);
            compilations = compilationRepository.findAll(predicate, pageRequest).getContent();
        }
        return compilations.stream()
                .map(CompilationMapper.INSTANCE::toDto)
                .toList();
    }

    /**
     * Создает новую подборку событий.
     *
     * @param adminCompilationDto DTO подборки событий
     * @return созданный DTO подборки событий
     */
    @Override
    public CompilationDto create(AdminCompilationDto adminCompilationDto) {
        log.info("Создание новой подборки событий с названием: {} и количеством событий: {}",
                adminCompilationDto.getTitle(), adminCompilationDto.getEvents().size());
        Compilation compilation = CompilationMapper.INSTANCE.toEntity(adminCompilationDto);
        if (!adminCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(eventService.getAllEventByIds(adminCompilationDto.getEvents()));
        }
        return CompilationMapper.INSTANCE.toDto(compilationRepository.save(compilation));
    }

    /**
     * Обновляет подборку событий по идентификатору.
     *
     * @param id идентификатор подборки событий
     * @param adminCompilationDto DTO подборки событий
     * @return обновленный DTO подборки событий
     */
    @Override
    public CompilationDto update(AdminCompilationDto adminCompilationDto, Long id) {
        log.info("Обновление подборки событий с id: {}", id);
        Compilation compilation = getById(id);
        CompilationMapper.INSTANCE.updateCompilationFromDto(adminCompilationDto, compilation);
        if (!adminCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(eventService.getAllEventByIds(adminCompilationDto.getEvents()));
        }
        return CompilationMapper.INSTANCE.toDto(compilationRepository.save(compilation));
    }

    /**
     * Удаляет подборку событий по идентификатору.
     *
     * @param id идентификатор подборки событий
     */
    @Override
    public void delete(Long id) {
        log.info("Удаление подборки событий с id: {}", id);
        if (compilationRepository.existsById(id)) {
            compilationRepository.deleteById(id);
        } else {
            throw new NotFoundException("Не удалось найти подборку событий с id: " + id);
        }
    }

    private Compilation getById(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Не удалось найти подборку событий с id: " + id));
    }

    private Predicate compilationPredicate(CompilationDtoGetParam param, QCompilation compilation) {
        if (param.getPinned() == null) {
            return null;
        }
        return compilation.pinned.eq(param.getPinned());
    }
}