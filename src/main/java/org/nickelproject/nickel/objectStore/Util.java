package org.nickelproject.nickel.objectStore;

import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;

public final class Util {

    private Util() {
        // Prevents construction
    }
    
    public static byte[] bytesFromObject(final Serializable object) {
        return SerializationUtils.serialize(object);
    }

    public static Object objectFromStream(final InputStream stream) {
        return SerializationUtils.deserialize(stream);
    }
}
