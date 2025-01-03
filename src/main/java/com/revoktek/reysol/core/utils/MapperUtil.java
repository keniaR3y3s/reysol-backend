package com.revoktek.reysol.core.utils;

import com.revoktek.reysol.core.exceptions.MapperParseObjectException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapperUtil {


    @Autowired
    private  ModelMapper modelMapper;



    public <T> T parseBetweenObject(Class<T> clazz, Object in) throws MapperParseObjectException {
        try {
            return modelMapper.map(in, clazz);
        } catch (Exception ex) {
           log.error("Error mapping object: {}", ex.getMessage(), ex);
            throw new MapperParseObjectException(ex);
        }
    }

    public <T> List<T> parseBetweenList(Class<T> clazz, List<?> source) throws MapperParseObjectException {
        try {
            return source.parallelStream()
                    .map(item -> {
                        try {
                            return parseBetweenObject(clazz, item);
                        } catch (MapperParseObjectException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error mapping list: {}", ex.getMessage(), ex);
            throw new MapperParseObjectException(ex);
        }
    }
}
