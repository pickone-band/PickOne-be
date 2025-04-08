package com.PickOne.global.util.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<E, D> {

    // Entity → DTO 변환
    D toDto(E entity);

    // DTO → Entity 변환
    E toEntity(D dto);

    // Entity 리스트 → DTO 리스트 변환
    default List<D> toDtoList(List<E> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    // DTO 리스트 → Entity 리스트 변환
    default List<E> toEntityList(List<D> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
