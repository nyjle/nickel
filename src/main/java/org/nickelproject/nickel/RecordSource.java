package org.nickelproject.nickel;

import org.nickelproject.nickel.dataflow.Source;
import org.nickelproject.nickel.types.Record;
import org.nickelproject.nickel.types.RecordDataType;


public interface RecordSource extends Source<Record> {
    RecordDataType schema();
}
