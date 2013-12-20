/*
 * Copyright (c) 2013 Numerate, Inc
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
package org.nickelproject.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;

import com.google.common.io.ByteStreams;

/**
 * General I/O utilities. Contains utilities for:
 * <ul>
 * <li>Streaming data from input streams to output streams.
 * <li>Streaming data from readers to writers.
 * <li>Extracting all the bytes from an input stream.
 * <li>Serializing/deserializing objects to/from byte arrays.
 * <li>Deflating/inflating, gzipping/gunzipping, serhexing/unserhexing blobs of
 * byte array data.
 * <li>etc.
 * </ul>
 */
public final class IoUtil {
    private static final int kDefaultBufferSize = 1024;

    /**
     * A reasonable size to use for buffered [file] input to maximize
     * performance. Works well enough whether or not the underlying file is in
     * the Linux FS cache or not. See, for example, the "Drops" docs for how the
     * Linux FS cache can interfere with microbenchmarks of how to size this
     * value.
     */
    public static final int  kBigBufferSize     = 32 * 1024 * 1024;

    private IoUtil() {
        // Not instantiable
    }

    /**
     * Convert the passed in serializable object into a serialized byte array.
     */
    public static byte[] serialize(final Object pObject) throws IOException {
        final ByteArrayOutputStream vByteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream vObjectOutputStream = new ObjectOutputStream(vByteArrayOutputStream);
        vObjectOutputStream.writeObject(pObject);
        vObjectOutputStream.close();
        final byte[] vResult = vByteArrayOutputStream.toByteArray();
        return vResult;
    }

    /**
     * Convert the passed in byte array into a serializable object.
     */
    public static <T> T deserialize(final byte[] pBytes) throws IOException {
        final ByteArrayInputStream vInputStream = new ByteArrayInputStream(pBytes);
        final T vObject = (T) deserialize(vInputStream);
        return vObject;
    }

    /**
     * Converts the specified input stream into a Serializable object.
     */
    public static <T> T deserialize(final InputStream pInputStream) throws IOException {
        try {
            final ObjectInputStream vObjectInputStream = new ObjectInputStream(pInputStream);
            final T vSerializable = (T) vObjectInputStream.readObject();
            // Note that the ObjectInputStream does not get closed because it
            // would
            // then close the parameter InputStream. Not closing the stream
            // should
            // be harmless however.
            return vSerializable;
        } catch (final ClassNotFoundException e) {
            throw RethrownException.rethrow(e);
        }
    }

    /**
     * Compress the given byte array using the deflate algorithm and return the
     * compressed byte array.
     */
    public static byte[] deflate(final byte[] pInput) throws IOException {
        final ByteArrayOutputStream vByteArrayStream = new ByteArrayOutputStream(pInput.length);
        final OutputStream vDeflaterStream = new DeflaterOutputStream(vByteArrayStream);
        vDeflaterStream.write(pInput);
        vDeflaterStream.close();
        final byte[] vCompressedBytes = vByteArrayStream.toByteArray();
        return vCompressedBytes;
    }

    /**
     * Uncompress the given byte array using the delfate algorithm and return
     * the uncompressed byte array.
     */
    public static byte[] inflate(final byte[] pInput) throws IOException {
        final ByteArrayInputStream vByteArrayStream = new ByteArrayInputStream(pInput);
        final ByteArrayOutputStream vByteArrayOutputStream = new ByteArrayOutputStream(pInput.length);
        final InputStream vInflaterStream = new InflaterInputStream(vByteArrayStream);
        ByteStreams.copy(vInflaterStream, vByteArrayOutputStream);
        vInflaterStream.close();
        final byte[] vUnCompressedBytes = vByteArrayOutputStream.toByteArray();
        return vUnCompressedBytes;
    }

    // These next two methods could be factored w/ the above methods using
    // Inflaters but you have to be very careful not to leak native memory.
    // Don't bother for now.

    /**
     * Compress the given byte array using the gzip algorithm and return the
     * compressed byte array.
     */
    public static byte[] gzip(final byte[] pInput) throws IOException {
        final ByteArrayOutputStream vByteArrayStream = new ByteArrayOutputStream(pInput.length);
        final OutputStream vDeflaterStream = new GZIPOutputStream(vByteArrayStream);
        vDeflaterStream.write(pInput);
        vDeflaterStream.close();
        final byte[] vCompressedBytes = vByteArrayStream.toByteArray();
        return vCompressedBytes;
    }

    /**
     * Uncompress the given byte array using the gzip algorithm and return the
     * uncompressed byte array.
     */
    public static byte[] gunzip(final byte[] pInput) throws IOException {
        final ByteArrayInputStream vByteArrayStream = new ByteArrayInputStream(pInput);
        final ByteArrayOutputStream vByteArrayOutputStream = new ByteArrayOutputStream(pInput.length);
        final InputStream vInflaterStream = new GZIPInputStream(vByteArrayStream);
        ByteStreams.copy(vInflaterStream, vByteArrayOutputStream);
        vInflaterStream.close();
        final byte[] vUnCompressedBytes = vByteArrayOutputStream.toByteArray();
        return vUnCompressedBytes;
    }

    /**
     * Returns a serialized, deflated, hex-encoded version of the given object.
     */
    public static String serhex(final Object pObject) throws IOException {
        final byte[] vSerialized = IoUtil.serialize(pObject);
        final byte[] vCompressed = IoUtil.deflate(vSerialized);
        return Base64.encodeBase64String(vCompressed);
    }

    /**
     * Given a hex-encoded, deflated, serialized object, returns the object.
     */
    public static <T> T deserhex(final String pString) throws IOException {
        final byte[] vCompressed = Base64.decodeBase64(pString);
        final byte[] vSerialized = IoUtil.inflate(vCompressed);
        return IoUtil.<T>deserialize(vSerialized);
    }
}
