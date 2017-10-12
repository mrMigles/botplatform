package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.ComparatorByTime;
import ru.holyway.botplatform.core.entity.ComparatorByValue;
import ru.holyway.botplatform.core.entity.Record;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
@Order(3)
public class RecordsHandler implements MessageHandler {

    private List<Record> recordsList = new ArrayList<>();
    private Map<String, Long> currentRecordMap = new ConcurrentHashMap<>();

    @Autowired
    private DataHelper dataHelper;

    @PostConstruct
    public void postConstruct() {
        initRecors();
    }


    @Override
    public String provideAnswer(final MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.containsIgnoreCase(mes, "Пахом, старт ")) {
            if (mes.length() > 14) {
                final String peopleID = mes.substring(13);
                if (!StringUtils.isEmpty(peopleID)) {
                    if (currentRecordMap.get(peopleID) != null) {
                        return "Я уже считаю время для: " + peopleID;
                    } else {
                        currentRecordMap.put(peopleID, System.currentTimeMillis());
                        return "Начал считать для:  " + peopleID;
                    }
                }
            }
            return null;
        }

        if (StringUtils.containsIgnoreCase(mes, "Пахом, стоп ")) {
            if (mes.length() > 13) {
                final String peopleID = mes.substring(12);
                if (!StringUtils.isEmpty(peopleID)) {
                    if (currentRecordMap.get(peopleID) != null) {
                        long currentRecord = System.currentTimeMillis() - currentRecordMap.get(peopleID);
                        long maximumRecord = recordsList.isEmpty() ? 0 : recordsList.get(0).time;
                        List<Record> recordForName = getRecordForName(peopleID);
                        recordForName.sort(new ComparatorByValue());
                        long recordForUser = recordForName.isEmpty() ? 0 : recordForName.get(0).time;
                        if (currentRecord > maximumRecord) {
                            return "!!! Новый обший рекорд установил(а) " + peopleID + ", время: " + TimeUnit.MILLISECONDS.toMinutes(currentRecord) + " минут. !!!";
                        } else if (currentRecord > recordForUser) {
                            return "Новый личный рекорд для " + peopleID + ", время: " + TimeUnit.MILLISECONDS.toMinutes(currentRecord) + " минут.";
                        }
                        recordsList.add(new Record(peopleID, currentRecord, System.currentTimeMillis()));
                        dataHelper.updateRecords(recordsList);
                        currentRecordMap.remove(peopleID);
                        Collections.sort(recordsList);
                        return peopleID + " проедржался(ась) " + TimeUnit.MILLISECONDS.toMinutes(currentRecord) + " минут.";

                    } else {
                        return "Я ещё не начинал считать для  " + peopleID;
                    }
                }
            }
            return null;
        }

        if (StringUtils.containsIgnoreCase(mes, "Пахом, сколько")) {

            if (currentRecordMap.size() > 0) {
                String message = "Сейчас считаю время для:";
                for (Map.Entry<String, Long> records : currentRecordMap.entrySet()) {
                    message += "\n" + records.getKey() + " - " + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - records.getValue()) + " минут";
                }
                return message;

            } else {
                return "Сейчас никто не идёт на рекорд";
            }
        }

        if (StringUtils.containsIgnoreCase(mes, "Пахом, отмена ")) {
            if (mes.length() > 15) {
                final String peopleID = mes.substring(14);
                if (!StringUtils.isEmpty(peopleID)) {
                    if (currentRecordMap.get(peopleID) != null) {
                        currentRecordMap.remove(peopleID);
                        return "Ok";
                    } else {
                        return "Нечего отменять";
                    }
                } else {
                    return "Нечего отменять";
                }
            }
            return null;
        }

        if (StringUtils.containsIgnoreCase(mes, "Пахом, рекорд")) {

            if (recordsList.size() > 0) {
                String message = "Рекорды: ";
                for (Record records : getRecordsAll()) {
                    message += "\n" + records.name + "\t - " + (records.date != null ? new Date(records.date).toString() : "давным давно") + "\t - " + TimeUnit.MILLISECONDS.toMinutes(records.time) + " минут";
                }
                return message;

            } else {
                return "Нет рекордов";
            }
        }
        if (StringUtils.containsIgnoreCase(mes, "Пахом, статистика ")) {
            if (mes.length() > 19) {
                final String peopleID = mes.substring(18);
                if (!StringUtils.isEmpty(peopleID)) {
                    List<Record> recordForName = getRecordForName(peopleID);
                    if (!recordForName.isEmpty()) {
                        String message = "Статистика для " + peopleID + ":";
                        long sum = 0;
                        for (Record records : recordForName) {
                            message += "\n" + (records.date != null ? new Date(records.date).toString() : "давным давно") + "\t - " + TimeUnit.MILLISECONDS.toMinutes(records.time) + " минут";
                            sum += records.time;
                        }
                        message += "\n\nОбщее время: " + TimeUnit.MILLISECONDS.toMinutes(sum) + " минут.";
                        return message;
                    } else {
                        return "Этот ещё салага, у него всё впереди";
                    }
                } else {
                    return "Пустота была всегда... ещё до даоса.";
                }
            }
            return null;
        }

        if (StringUtils.containsIgnoreCase(mes, "Пахом, статистика")) {

            if (recordsList.size() > 0) {
                String message = "Статистика: ";
                List<Record> statisticsList = new ArrayList<>(recordsList);
                statisticsList.sort(new ComparatorByTime());
                for (Record records : statisticsList) {
                    message += "\n" + (records.date != null ? new Date(records.date).toString() : "давным давно") + "\t - " + records.name + "\t - " + TimeUnit.MILLISECONDS.toMinutes(records.time) + " минут";
                }
                return message;

            } else {
                return "Нет рекордов";
            }
        }
        return null;
    }

    private synchronized void initRecors() {
        recordsList.clear();
        recordsList.addAll(dataHelper.getRecords());
    }

    private List<Record> getRecordsAll() {
        Map<String, Record> recordsMap = new HashMap<>();
        for (Record record : recordsList) {
            if (recordsMap.get(record.name) != null) {
                if (record.time > recordsMap.get(record.name).time) {
                    recordsMap.put(record.name, record);
                }
            } else {
                recordsMap.put(record.name, record);
            }

        }
        List<Record> records = new ArrayList<>(recordsMap.values());
        records.sort(new ComparatorByValue());
        return records;
    }


    private List<Record> getRecordForName(final String name) {
        List<Record> recordForName = new ArrayList<>();
        for (Record record : recordsList) {
            if (record.name.equals(name)) {
                recordForName.add(record);
            }
        }
        recordForName.sort(new ComparatorByTime());
        return recordForName;
    }

}
