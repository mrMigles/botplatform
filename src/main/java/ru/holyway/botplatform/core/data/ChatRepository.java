package ru.holyway.botplatform.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.holyway.botplatform.core.entity.Chat;

public interface ChatRepository extends MongoRepository<Chat, String> {

}
