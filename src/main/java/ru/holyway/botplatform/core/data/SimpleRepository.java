package ru.holyway.botplatform.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.holyway.botplatform.core.entity.SimpleDictionary;

/**
 * Created by Sergey on 4/24/2017.
 */
public interface SimpleRepository extends MongoRepository<SimpleDictionary, String> {

}
