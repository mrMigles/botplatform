package ru.holyway.botplatform.config;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergey on 11/30/2016.
 */
public class JobInitializer {

    private final TaskScheduler scheduler;

    private static final long DELAY_TO_UPDATE = TimeUnit.MINUTES.toMillis(28);

    public JobInitializer() {
        this.scheduler = new ConcurrentTaskScheduler();
        try {
            new RestTemplate().getForObject(new URI("https://botplatformpakhom.herokuapp.com/"), String.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void job() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            private int num = 0;

            @Override
            public void run() {
                if (num < 24) {
                    try {
                        new RestTemplate().getForObject(new URI("https://botplatformpakhom.herokuapp.com/"), String.class);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    num++;
                }
            }
        }, DELAY_TO_UPDATE);
    }
}
