package org.nickelproject.util.sources;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.util.RethrownException;

public final class FileLineSource implements Source<String> {
    private static final long serialVersionUID = 1L;
    private final String      fileName;

    public FileLineSource(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Iterator<String> iterator() {
        return new FileLineIterator();
    }

    private final class FileLineIterator implements Iterator<String> {
        private final BufferedReader mReader;

        public FileLineIterator() {
            try {
                mReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            } catch (final Exception e) {
                System.out.println("Can't find file: " + fileName);
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
            System.out.println("FileLineSource: " + vRetVal);
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

        @Override
        public void remove() {
            throw new RuntimeException("Cannot remove from a source iterator");
        }

    }

    @Override
    public Source<? extends Source<String>> partition(final int partitionSize) {
        return Sources.singleton(this);
    }
}
