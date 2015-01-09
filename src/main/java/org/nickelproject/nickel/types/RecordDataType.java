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
package org.nickelproject.nickel.types;

import java.util.List;
import java.util.Map;

import org.nickelproject.util.CollectionUtils;
import org.nickelproject.util.tuple.Pair;
import org.nickelproject.util.tuple.Triple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This class provides the schema for a record.
 * That is it maintains a mapping from field names to their types and to their indices.
 */
public final class RecordDataType implements DataType {
    private static final long serialVersionUID = 1L;
    private final Map<String, DataType> memberTypes   = Maps.newHashMap();
    private final Map<Integer, String>  memberIndices = Maps.newHashMap();
    private final Map<String, Integer>  nameToIndex   = Maps.newHashMap();

    private RecordDataType(final Iterable<Triple<Integer, String, DataType>> members) {
        for (final Triple<Integer, String, DataType> member : members) {
            memberTypes.put(member.getB(), member.getC());
            memberIndices.put(member.getA(), member.getB());
            nameToIndex.put(member.getB(), member.getA());
        }
    }

    public static final class Builder {
        private final List<Triple<Integer, String, DataType>> members = Lists.newArrayList();
        private int                                           counter = 0;

        public Builder add(final String name, final DataType type) {
            members.add(Triple.of(counter++, name, type));
            return this;
        }
        
        public Builder add(final String name, final Class javaClass) {
            return add(name, new JavaClassDataType(javaClass));
        }

        public RecordDataType build() {
            return new RecordDataType(members);
        }
    }

    @Override
    public <S, T> S visit(final DataTypeVisitor<S, T> visitor, final T data) {
        return visitor.visit(this, data);
    }

    public int getIndex(final String name) {
        return nameToIndex.get(name);
    }

    public String getName(final int index) {
        return memberIndices.get(index);
    }

    public DataType getType(final int index) {
        return memberTypes.get(getName(index));
    }

    public Iterable<Pair<String, DataType>> getMembers() {
        final List<Pair<String, DataType>> retVal = Lists.newArrayList();
        for (final Integer index : CollectionUtils.asSortedList(memberIndices.keySet())) {
            final String name = memberIndices.get(index);
            retVal.add(Pair.of(name, memberTypes.get(name)));
        }
        return retVal;
    }
    
    @Override
    public String toString() {
        return getMembers().toString();
    }
}
