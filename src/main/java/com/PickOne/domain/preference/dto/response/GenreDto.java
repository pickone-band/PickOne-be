package com.PickOne.domain.preference.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreDto {
    private String name;

    public GenreDto(String name) {
        this.name = name;
    }
}