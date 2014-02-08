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
package org.nickelproject.util.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.streamUtil.InputStreamFactory;

import com.google.common.collect.UnmodifiableIterator;

public final class FileLineSource implements Source<String> {
    private static final long serialVersionUID = 1L;
    private final InputStreamFactory inputStreamFactory;

    public FileLineSource(final InputStreamFactory inputStreamFactory) {
        this.inputStreamFactory = inputStreamFactory;
    }
    
    @Override
    public int size() {
        return Source.UNKOWN_SIZE;
    }

    @Override
    public Iterator<String> iterator() {
        return new FileLineIterator();
    }

    private final class FileLineIterator extends UnmodifiableIterator<String> {
        private final BufferedReader mReader;

        public FileLineIterator() {
            try {
                mReader = new BufferedReader(new InputStreamReader(inputStreamFactory.getInputStream(), "UTF8"));
            } catch (UnsupportedEncodingException e) {
                throw RethrownException.rethrow(e);
            }
        }

        @Override
        public boolean hasNext() {
            boolean vRetVal;
            try {
                vRetVal = mReader.ready();
            } catch (IOException e) {
                throw RethrownException.rethrow(e);
            }
            return vRetVal;
        }

        @Override
        public String next() {
            String vRetVal;
            try {
                vRetVal = mReader.readLine();
            } catch (IOException e) {
                throw RethrownException.rethrow(e);
            }
            return vRetVal;
        }
    }

    @Override
    public Source<? extends Source<String>> partition(final int partitionSize) {
        return Sources.singleton(this);
    }
}
