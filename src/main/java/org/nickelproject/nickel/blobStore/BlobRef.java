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
package org.nickelproject.nickel.blobStore;

import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Preconditions;

/**
 * Contains the name of an entry in a BlobStore.
 *
 * @author nigelduffy
 */
public final class BlobRef implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String      cryptoHash;

    private BlobRef(final String cryptoHash) {
        Preconditions.checkArgument(cryptoHash != null);
        Preconditions.checkArgument(cryptoHash.length() > 0);
        this.cryptoHash = cryptoHash;
    }

    public static BlobRef of(final String cryptoHash) {
        return new BlobRef(cryptoHash);
    }

    @Override
    public String toString() {
        return cryptoHash;
    }

    @Override
    public int hashCode() {
        return cryptoHash.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlobRef other = (BlobRef) obj;
        if (!cryptoHash.equals(other.cryptoHash)) {
            return false;
        }
        return true;
    }
    
    public static BlobRef keyFromBytes(final byte[] bytes) {
        return BlobRef.of(DigestUtils.sha256Hex(bytes));
    }
}
