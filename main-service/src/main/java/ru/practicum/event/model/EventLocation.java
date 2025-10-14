package ru.practicum.event.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLocation {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
