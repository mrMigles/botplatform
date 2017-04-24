package ru.holyway.botplatform.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.holyway.botplatform.core.entity.JSettings;

/**
 * Created by Sergey on 4/24/2017.
 */
public interface SettingsRepository extends MongoRepository<JSettings, String> {

}
