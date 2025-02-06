package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.View;

public interface ViewRepository extends JpaRepository<View, Long> {
}
