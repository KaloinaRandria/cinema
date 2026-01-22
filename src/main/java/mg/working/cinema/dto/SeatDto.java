package mg.working.cinema.dto;

public record SeatDto(
        String id,
        String rangee,
        Integer numero,
        String type
) {}