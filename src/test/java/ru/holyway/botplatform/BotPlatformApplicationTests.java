package ru.holyway.botplatform;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

public class BotPlatformApplicationTests {

    @Test
    public void contextLoads() throws IOException, ParseException {
        String url = "https://api.telegram.org/file/bot275590037:AAGkJCYGcOyNxPqlWnHmktYod8vWOMCHZYM/photos/file_2.jpg";
        new URL(url);
        final BufferedImage bufferedImage = ImageIO.read(new URL("url"));
    }
}
