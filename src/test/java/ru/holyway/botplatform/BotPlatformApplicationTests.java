package ru.holyway.botplatform;

import com.google.gson.Gson;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BotPlatformApplicationTests {

	@Test
	public void contextLoads() throws IOException {
		List<String> simpleWords = Files.readAllLines(Paths.get("F:\\sub\\sub.txt"), StandardCharsets.UTF_8);
		for (String s : simpleWords){
			byte[] ptext = Charset.forName("UTF-8").encode(s).array();
			s = new String(ptext);
			System.out.println(s);
		}
		System.out.println(new Gson().toJson(simpleWords));
	}

}
