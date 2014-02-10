package org.nickelproject.util.functions;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public final class ToListFunction<T> implements Function<T, List<T>> {

    @Override
    public List<T> apply(@Nonnull final T input) {
        return Collections.singletonList(input);
    }
}
