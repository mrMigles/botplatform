package ru.holyway.botplatform.core.education;

import ru.holyway.botplatform.core.ProcessStopException;

/**
 * Created by seiv0814 on 12-10-17.
 */
public class EducationActiveException extends ProcessStopException {

    public EducationActiveException(final String message) {
        super(message);
    }
}
