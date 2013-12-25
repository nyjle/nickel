package org.nickelproject.util.sources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.dataflow.Sources;
import org.nickelproject.util.RethrownException;

public class FileLineSource implements Source<String> {
	public final String mFileName;
	
	public FileLineSource(String pFileName) {
		mFileName = pFileName;
	}
	
	@Override
	public Iterator<String> iterator() {
		return new FileLineIterator();
	}
	
	private final class FileLineIterator implements Iterator<String> {
		private final BufferedReader mReader;
		
		public FileLineIterator() {
			try {
				mReader = new BufferedReader(new FileReader(mFileName));
			} catch (FileNotFoundException e) {
				System.out.println("Can't find file: " + mFileName);
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
