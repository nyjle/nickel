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
package org.nickelproject.util.streamUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.nickelproject.util.RethrownException;

public final class FileInputStreamFactory implements InputStreamFactory {
    private static final long serialVersionUID = 1L;
    private final String mFileName;

    public FileInputStreamFactory(final String pFileName) {
        mFileName = pFileName;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(mFileName);
        } catch (final FileNotFoundException e) {
            throw RethrownException.rethrow(e);
        }
    }
}
