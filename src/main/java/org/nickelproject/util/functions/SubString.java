package org.nickelproject.util.functions;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

public final class SubString implements Function<String, String> {
    private final int start;
    private final int end;
    
    public SubString(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String apply(@Nonnull final String input) {
        return input.substring(start, end);
    }
}
