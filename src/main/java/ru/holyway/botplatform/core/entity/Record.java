package ru.holyway.botplatform.core.entity;

import org.springframework.data.annotation.Id;

/**
 * Created by seiv0814 on 04-09-17.
 */
public class Record implements Comparable<Record> {

    @Id
    public String id;

    public Long time;

    public Record(String id, Long time) {
        this.id = id;
        this.time = time;
    }


    @Override
    public int compareTo(Record o) {
        if (o.time > this.time) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        return id != null ? id.equals(record.id) : record.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
