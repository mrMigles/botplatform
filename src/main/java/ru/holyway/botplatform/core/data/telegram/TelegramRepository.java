package ru.holyway.botplatform.core.data.telegram;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.io.Serializable;

public interface TelegramRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
}
