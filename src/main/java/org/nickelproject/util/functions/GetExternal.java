package org.nickelproject.util.functions;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;

public final class GetExternal<T> implements Function<ExternalReference<T>, T> {

    @Override
    public T apply(@Nonnull final ExternalReference<T> input) {
        return input.get();
    }
}
