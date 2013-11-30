package org.nickelproject.util.functions;

import com.google.common.base.Function;

public class SubString implements Function<String, String> {
    private final int start;
    private final int end;
    
    public SubString(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String apply(final String input) {
        return input.substring(start, end);
    }
}
