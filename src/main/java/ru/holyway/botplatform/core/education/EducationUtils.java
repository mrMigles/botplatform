package ru.holyway.botplatform.core.education;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by seiv0814 on 10-10-17.
 */
public class EducationUtils {

    public static List<String> getTokenizeMessage(String message) {

        StringTokenizer tok = new StringTokenizer(message, " ,;-!.?()…");
        ArrayList<String> a = new ArrayList<>();
        while (tok.hasMoreTokens()) {
            a.add(tok.nextToken());
        }
        return a;
    }
}
