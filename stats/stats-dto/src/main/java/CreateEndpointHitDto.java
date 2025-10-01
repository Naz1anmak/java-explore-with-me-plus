import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public record CreateEndpointHitDto(
        Long id,

        @NotBlank(message = "Название приложения не может быть пустым")
        String app,

        @NotBlank(message = "URI не может быть пустым")
        String uri,

        @NotBlank(message = "IP адрес не может быть пустым")
        String ip,

        @NotNull(message = "Временная метка не может быть null")
        @CreationTimestamp
        LocalDateTime timestamp
) {
}