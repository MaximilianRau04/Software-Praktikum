package com.sopra.eaplanner.converters;

import com.sopra.eaplanner.reward.Reward;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a {@link String} representation of a reward type to its corresponding
 * {@link Reward.Type} enum value. This class is used as a custom converter in Spring
 * for automatic type conversion in request parameters or other contexts.
 *
 * <p>Example usage:</p>
 * <pre>
 *   StringToRewardTypeConverter converter = new StringToRewardTypeConverter();
 *   Reward.Type type = converter.convert("bonus");
 *   // type == Reward.Type.BONUS
 * </pre>
 *
 * <p>The input string is normalized to uppercase and trimmed before conversion. If the input
 * does not match any of the {@link Reward.Type} values, an {@link IllegalArgumentException}
 * is thrown.</p>
 *
 * <p>This class is registered as a Spring component and can be automatically used by the
 * Spring ConversionService.</p>
 */
@Component
public class StringToRewardTypeConverter implements Converter<String, Reward.Type> {

    /**
     * Converts the given {@link String} to a {@link Reward.Type} enum value.
     *
     * @param source the input string representing a reward type; cannot be null or empty
     * @return the corresponding {@link Reward.Type} enum value
     * @throws IllegalArgumentException if the input string does not match any valid
     *                                  {@link Reward.Type} value
     */
    @Override
    public Reward.Type convert(String source) {
        try {
            return Reward.Type.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Reward.Type value: " + source);
        }
    }
}
