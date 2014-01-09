package org.nickelproject.util.functions;

import org.nickelproject.nickel.externalReference.ExternalReference;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public final class FunctionUtil {

    private FunctionUtil() {
        // Prevents construction
    }
    
    public static <F, T> Function<ExternalReference<F>, ExternalReference<T>>
                            externalize(final Function<F, T> function) {
        return Functions.compose(new PutExternal<T>(), Functions.compose(function, new GetExternal<F>()));
    }
}
