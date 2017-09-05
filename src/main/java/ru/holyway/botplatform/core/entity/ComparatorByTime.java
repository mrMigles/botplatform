package ru.holyway.botplatform.core.entity;

import java.util.Comparator;

/**
 * Created by Sergey on 9/5/2017.
 */
public class ComparatorByTime implements Comparator<Record> {

    @Override
    public int compare(Record o1, Record o2) {
        if (o1.date > o2.date) {
            return 1;
        } else {
            return -1;
        }
    }
}
