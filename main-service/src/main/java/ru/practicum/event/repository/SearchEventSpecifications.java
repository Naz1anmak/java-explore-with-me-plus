package ru.practicum.event.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public class SearchEventSpecifications {

    public static Specification<Event> addWhereNull() {
        return (root, query, criteriaBuilder) -> null;
    }

    public static Specification<Event> addWhereUsers(List<Long> userIds) {
        return (root, query, criteriaBuilder) -> {
            if (userIds == null || userIds.isEmpty()) return criteriaBuilder.conjunction();
            return root.get("initiator").get("id").in(userIds);
        };
    }

    public static Specification<Event> addWhereStates(List<String> states) {
        return (root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) return criteriaBuilder.conjunction();
            return root.get("state").in(states);
        };
    }

    public static Specification<Event> addWhereCategories(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) return criteriaBuilder.conjunction();
            return root.get("category").get("id").in(categoryIds);
        };
    }

    public static Specification<Event> addWhereStartsBefore(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) -> {
            if (dateTime == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), dateTime);
        };
    }

    public static Specification<Event> addWhereEndsAfter(LocalDateTime dateTime) {
        return (root, query, criteriaBuilder) -> {
            if (dateTime == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), dateTime);
        };
    }

    public static Specification<Event> addLikeText(String text) {
        return (root, query, criteriaBuilder) -> {
            if (text == null || text.trim().isEmpty()) return criteriaBuilder.conjunction();
            String searchText = "%" + text.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)
            );
        };
    }

    public static Specification<Event> isPaid(Boolean paid) {
        return (root, query, criteriaBuilder) -> {
            if (paid == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("isPaid"), paid);
        };
    }

    public static Specification<Event> addWhereAvailableSlots() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("participantLimit"), 0),
                        criteriaBuilder.greaterThan(
                                root.get("participantLimit"),
                                root.get("confirmedRequests")
                        )
                );
    }
}
