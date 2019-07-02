package TouchSoftLabs.Converter;

import java.util.HashSet;
import java.util.Set;

public interface EntityConverter<T, B> {

    T convertToDto(final B entity);

    B convertToEntity(final T dto);

    default Set<T> convertToDto(final Set<B> entitySet) {
        if (entitySet != null) {
            final Set<T> dtoSet = new HashSet<>();
            for (final B entity : entitySet) {
                final T dto = convertToDto(entity);
                dtoSet.add(dto);
            }
            return dtoSet;
        }
        return null;
    }

    default Set<B> convertToEntity(final Set<T> dtoSet) {
        if (dtoSet != null) {
            final Set<B> entitySet = new HashSet<>();
            for (final T dto : dtoSet) {
                final B entity = convertToEntity(dto);
                entitySet.add(entity);
            }
            return entitySet;
        }
        return null;
    }


}