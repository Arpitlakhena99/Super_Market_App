package com.example.supermarket.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    private final ModelMapper mapper;

    public DtoMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public <S, T> T map(S source, Class<T> targetClass) {
        return mapper.map(source, targetClass);
    }
}

