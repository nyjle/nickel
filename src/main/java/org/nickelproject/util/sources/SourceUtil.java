package org.nickelproject.util.sources;

/*
 * Copyright (c) 2013 Nigel Duffy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.nickelproject.nickel.dataflow.MapReduceUtil;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.nickel.mapReduce.Mapper;
import org.nickelproject.nickel.mapReduce.SynchronousMapper;
import org.nickelproject.util.CloseableIterator;
import org.nickelproject.util.TrivialCloseableIterator;
import org.nickelproject.util.functions.ToListFunction;
import org.nickelproject.util.reducers.MergeListReducer;

public final class SourceUtil {

    private SourceUtil() {
        // Prevents construction
    }
    
    public static <T> List<T> toList(final Source<T> source) {
        final Mapper mapper = new SynchronousMapper();
        return MapReduceUtil.mapReduce(source, new ToListFunction<T>(), new MergeListReducer<T>(), mapper);
    }
    
    public static Source<File> files(final String directoryName, final String[] suffices, 
            final boolean recursive) {
        return new Source<File>() {

            @Override
            public CloseableIterator<File> iterator() {
                return TrivialCloseableIterator.create(
                        FileUtils.iterateFiles(new File(directoryName), suffices, recursive));
            }

            @Override
            public Source<? extends Source<File>> partition(final int sizeGuideline) {
                return Sources.singleton(this);
            }

            @Override
            public int size() {
                return Source.unknownSize;
            }
        };
    }
}
