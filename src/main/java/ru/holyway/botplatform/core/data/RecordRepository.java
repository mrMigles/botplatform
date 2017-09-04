package ru.holyway.botplatform.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.holyway.botplatform.core.entity.Record;

/**
 * Created by seiv0814 on 04-09-17.
 */
public interface RecordRepository extends MongoRepository<Record, String> {

}
