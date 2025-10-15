package ru.practicum.event.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
public class EventLocation {
    @NotNull(message = "Широта не может быть null")
    private Float lat;

    @NotNull(message = "Долгота не может быть null")
    private Float lon;
}
