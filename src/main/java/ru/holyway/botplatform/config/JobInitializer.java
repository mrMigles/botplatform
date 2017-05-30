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
    public static int ITER = 0;
    private static final long DELAY_TO_UPDATE = TimeUnit.MINUTES.toMillis(28);

    public JobInitializer() {
        this.scheduler = new ConcurrentTaskScheduler();
    }

    @PostConstruct
    private void job() {
        scheduler.scheduleWithFixedDelay(new Runnable() {
            private int num = ITER;

            @Override
            public void run() {
                num = ITER;
                if (num < 24) {
                    try {
                        new RestTemplate().getForObject(new URI("https://botplatformpakhom.herokuapp.com"), String.class);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    ITER++;
                }
            }
        }, DELAY_TO_UPDATE);
    }
}
