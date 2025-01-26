package com.sopra.eaplanner.converters;

import com.sopra.eaplanner.reward.Reward;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRewardTypeConverter implements Converter<String, Reward.Type> {

    @Override
    public Reward.Type convert(String source) {
        try {
            return Reward.Type.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Reward.Type value: " + source);
        }
    }
}
