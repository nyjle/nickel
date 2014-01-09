package org.nickelproject.util.functions;

import java.io.Serializable;

import javax.annotation.Nonnull;

import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;

public final class GetExternal<T> implements Function<ExternalReference<T>, T>, Serializable {

    @Override
    public T apply(@Nonnull final ExternalReference<T> input) {
        return input.get();
    }
}