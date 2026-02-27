package ru.holyway.botplatform.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.holyway.botplatform.core.entity.ChatMembers;

public interface ChatMemberRepository extends MongoRepository<ChatMembers, String> {

}
