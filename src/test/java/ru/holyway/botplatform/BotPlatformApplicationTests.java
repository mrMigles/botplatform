package ru.holyway.botplatform;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotPlatformApplicationTests {

    @Test
    public void contextLoads() throws IOException, ParseException {
        String input = "Sep 04, 2017";
        SimpleDateFormat parser = new SimpleDateFormat("MMM d, yyyy");
        Date date = parser.parse(input);
        System.out.println(date.getTime());
    }
}
