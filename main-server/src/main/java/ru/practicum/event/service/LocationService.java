package ru.practicum.event.service;

import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

public interface LocationService {
    Location getLocation(Location location);
}
