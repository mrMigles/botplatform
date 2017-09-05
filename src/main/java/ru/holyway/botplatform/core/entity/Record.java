package ru.holyway.botplatform.core.entity;

/**
 * Created by seiv0814 on 04-09-17.
 */
public class Record implements Comparable<Record> {


    public String name;

    public Long time;

    public Long date;

    public Record(String id, Long time, Long date) {
        this.name = id;
        this.time = time;
        this.date = date;
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

        return name != null ? name.equals(record.name) : record.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
