package com.sopra.eaplanner.converters;

import com.sopra.eaplanner.event.ExperienceLevel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToExperienceLevelConverter implements Converter<String, ExperienceLevel> {

    @Override
    public ExperienceLevel convert(String source) {
        try {
            return ExperienceLevel.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Experience Level value: " + source);
        }
    }
}
