package ru.holyway.botplatform.core.entity;

import java.util.Comparator;

/**
 * Created by Sergey on 9/5/2017.
 */
public class ComparatorByValue implements Comparator<Record> {

    @Override
    public int compare(Record o1, Record o2) {
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        if (o2.time > o1.time) {
            return 1;
        } else {
            return -1;
        }
    }
}
