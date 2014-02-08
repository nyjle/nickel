/*
 * Copyright (c) 2013 Numerate, Inc
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
package org.nickelproject.nickel.mapReduce;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.util.RethrownException;
import org.nickelproject.util.testUtil.UnitAnnotation;
import org.nickelproject.util.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

/**
 * Tests {@link Mapper} implementations.
 */
@UnitAnnotation
public abstract class BaseMapperTest {  
    private static Logger kLogger = LoggerFactory.getLogger(BaseMapperTest.class);
    
    @Test
    public abstract void testBasicFunctionality() throws Exception;
    
    @Test
    public abstract void testThroughput() throws Exception;
    
    protected static void testBasicFunctionality(final Mapper pMapper) {
        final int kNumElements = 10000;
        final Iterator<Pair<Integer, Integer>> vIterator = 
            pMapper.map(ContiguousSet.create(
                    Range.closedOpen(0, kNumElements), DiscreteDomain.integers()).iterator(),
                    new MutiplyFunction());
        
        final Set<Integer> vSeenSet = Sets.newHashSet();
        while (vIterator.hasNext()) {
            Integer vInput = null;
            final Pair<Integer, Integer> result = vIterator.next();
            vInput = result.getA();
            assertEquals(vInput.intValue() * MutiplyFunction.factor, result.getB().intValue());
            vSeenSet.add(vInput);
        }
        assertEquals(kNumElements, vSeenSet.size());
    }
        
    protected static long testMapper(final Mapper pMapper, final int pNumSamples, final int pPayloadSize, 
        final int pFunctorApplyMillis, final int pSourceLatency, final double pExpectedMillis) throws Exception {
        System.out.println(pExpectedMillis);
        final long runAwayMultiplier = 20;
        final double vMaxRunTime = pExpectedMillis * runAwayMultiplier; // Just in case we have something runaway...
        long vStart = System.currentTimeMillis();
        Iterator<Integer> vIterator = 
            pMapper.map(new RandomByteArraySource(pNumSamples, pPayloadSize, pSourceLatency).iterator(),
                    new SleeperFunction(pFunctorApplyMillis));
        
        vStart = System.currentTimeMillis();
        while (vIterator.hasNext()) {
            vIterator.next();
            Assert.assertTrue(System.currentTimeMillis() - vStart < vMaxRunTime);
        }
        final long vTime = System.currentTimeMillis() - vStart;
        Assert.assertTrue(vTime < pExpectedMillis);
        kLogger.debug("actual:" + vTime + " expected:" + pExpectedMillis);
        return vTime;
    }
    
    protected final Logger getLogger() {
        return kLogger;
    }
    
    private static class RandomByteArraySource implements Source<byte[]> {
        private static final long serialVersionUID = 1L;
        private final int mSourceSize;
        private final int mByteArraySize;
        private final int mLatency;

        RandomByteArraySource(final int pSourceSize, final int pByteArraySize, final int pLatency) {
            mSourceSize = pSourceSize;
            mByteArraySize = pByteArraySize;
            mLatency = pLatency;
        }

        @Override
        public int size() {
            return mSourceSize;
        }
        
        @Override
        public Source<Source<byte[]>> partition(final int partitionSize) {
            throw new RuntimeException("Not Implemented");
        }
        
        private class SourceIterator implements Iterator<byte[]> {
            private int mRemaining = mSourceSize;
            private final Random mRandom = new Random();
            
            @Override
            public boolean hasNext() {
                return mRemaining > 0;
            }

            @Override
            public byte[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final byte[] vBytes = new byte[mByteArraySize];
                mRandom.nextBytes(vBytes);
                mRemaining--;
                if (mLatency > 0) {
                    try {
                        Thread.sleep(mLatency);
                    } catch (InterruptedException e) {
                        kLogger.debug("InterruptedException Thrown", e);
                    }
                }
                return vBytes;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }  
        }

        @Override
        public Iterator<byte[]> iterator() {
            return new SourceIterator();
        }
    }
    
    private static class SleeperFunction implements Function<byte[], Integer> {
        private final long mSleepMillis;
        
        SleeperFunction(final long pSleepMillis) {
            mSleepMillis = pSleepMillis;
        }
        
        @Override
        public Integer apply(final byte[] pFrom) {
            try {
                Thread.sleep(mSleepMillis);
            } catch (final InterruptedException e) {
                throw RethrownException.rethrow(e);
            }
            return Integer.valueOf(pFrom.length);
        }
    }
    
    private static class MutiplyFunction implements Function<Integer, Pair<Integer, Integer>> {
        private static final int factor = 10;
        
        @Override
        public Pair<Integer, Integer> apply(final Integer pFrom) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw RethrownException.rethrow(e);
            }
            return Pair.of(pFrom, Integer.valueOf(pFrom.intValue() * factor));
        }
    }
}
