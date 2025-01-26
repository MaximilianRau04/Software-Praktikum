package com.sopra.eaplanner.converters;

import com.sopra.eaplanner.event.ExperienceLevel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a {@link String} representation of an experience level to its corresponding
 * {@link ExperienceLevel} enum value. This class is used as a custom converter in Spring
 * to handle automatic type conversion in request parameters or other contexts.
 *
 * <p>Example usage:</p>
 * <pre>
 *   StringToExperienceLevelConverter converter = new StringToExperienceLevelConverter();
 *   ExperienceLevel level = converter.convert("junior");
 *   // level == ExperienceLevel.JUNIOR
 * </pre>
 *
 * <p>The input string is normalized to uppercase and trimmed before conversion. If the input
 * does not match any of the {@link ExperienceLevel} values, an {@link IllegalArgumentException}
 * is thrown.</p>
 *
 * <p>This class is registered as a Spring component and can be automatically used by the
 * Spring ConversionService.</p>
 */
@Component
public class StringToExperienceLevelConverter implements Converter<String, ExperienceLevel> {

    /**
     * Converts the given {@link String} to an {@link ExperienceLevel} enum value.
     *
     * @param source the input string representing an experience level; cannot be null or empty
     * @return the corresponding {@link ExperienceLevel} enum value
     * @throws IllegalArgumentException if the input string does not match any valid
     *                                  {@link ExperienceLevel} value
     */
    @Override
    public ExperienceLevel convert(String source) {
        try {
            return ExperienceLevel.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Experience Level value: " + source);
        }
    }
}
