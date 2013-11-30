package org.nickelproject.util.functions;

import java.util.Collections;
import java.util.Map;

import org.nickelproject.util.tuple.Pair;
import com.google.common.base.Function;

public class ToMapFunction<T,S> implements Function<Pair<T, S>, Map<T,S>> {

	@Override
	public Map<T, S> apply(final Pair<T, S> pair) {
		return Collections.singletonMap(pair.getA(), pair.getB());
	}
}
