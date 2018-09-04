package ru.holyway.botplatform.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.holyway.botplatform.core.entity.Chat;

/**
 * Created by Sergey on 4/24/2017.
 */
public interface ChatRepository extends MongoRepository<Chat, String> {

  public Chat findById(String id);
}
