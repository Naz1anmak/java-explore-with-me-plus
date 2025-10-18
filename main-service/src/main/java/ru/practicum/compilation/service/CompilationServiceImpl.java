package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto request) {
        if (compilationRepository.existsByTitle(request.title()))
            throw new ConflictException("Сборник с таким названием (" + request.title() + ") уже существует");

        Compilation compilation = compilationMapper.toEntity(request);

        if (request.events() != null && !request.events().isEmpty()) {
            List<Event> events = eventRepository.findAllByEventIds(new ArrayList<>(request.events()));

            if (events.size() != request.events().size()) throw new NotFoundException("Не все события найдены");
            compilation.setEvents(new HashSet<>(events));
        } else compilation.setEvents(new HashSet<>());
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Создан сборник: {}", request);

        return compilationMapper.toDto(savedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId))
            throw new NotFoundException("Сборник с идентификатором " + compId + " не найден");

        log.info("Удален сборник compId={}", compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = getCompilationOrThrow(compId);

        if (request.title() != null && !request.title().equals(compilation.getTitle()) &&
            compilationRepository.existsByTitle(request.title()))
            throw new ConflictException("Сборник с таким названием (" + request.title() + ") уже существует");

        if (request.title() != null) compilation.setTitle(request.title());
        compilation.setPinned(request.pinned());

        if (request.events() != null) {
            if (request.events().isEmpty()) {
                compilation.setEvents(new HashSet<>());
            } else {
                List<Event> events = eventRepository.findAllByEventIds(new ArrayList<>(request.events()));
                if (events.size() != request.events().size())
                    throw new NotFoundException("Некоторые события не найдены");
                compilation.setEvents(new HashSet<>(events));
            }
        }

        log.info("Обновлен сборник request={}", request);

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("id").ascending()
        );

        Page<Compilation> compilationsPage;
        if (pinned != null)
            compilationsPage = compilationRepository.findByPinned(pinned, sortedPageable);
        else
            compilationsPage = compilationRepository.findAll(sortedPageable);

        List<Compilation> compilations = compilationsPage.getContent();

        if (!compilations.isEmpty()) {
            List<Long> compilationIds = compilations.stream()
                    .map(Compilation::getId)
                    .toList();

            log.info("Получен список сборников pinned={}, pageable={}", pinned, pageable);

            return compilations.stream()
                    .map(compilationMapper::toDto)
                    .collect(Collectors.toList());
        }
        log.info("Список сборников пуст");

        return List.of();
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getCompilationOrThrow(compId);

        log.info("Получен сборник compId={}", compId);

        return compilationMapper.toDto(compilation);
    }

    private Compilation getCompilationOrThrow(Long compId) {
        return compilationRepository.findByIdWithEvents(compId)
                .orElseThrow(() -> new NotFoundException("Сборник с идентификатором " + compId + " не найден"));
    }
}
