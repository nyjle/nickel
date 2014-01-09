package org.nickelproject.util.functions;

import java.io.Serializable;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;

public final class PutExternal<T> implements Function<T, ExternalReference<T>>, Serializable {

    @Override
    public ExternalReference<T> apply(@Nonnull final T input) {
        return ExternalReference.of(input);
    }
}
