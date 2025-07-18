package com.Elecciones.elections.dto;

public record OptionOut(
        Long id,
        String label,
        String votingEventId,
        String votingEventTitle
) {
}
