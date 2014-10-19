package org.jorge.twitterpromoter.io;

import org.jorge.twitterpromoter.io.net.TwitterManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TickerManager {

    private static final int HEARTBEAT_PERIOD_SECONDS = 60 * 15;
    private static final int TWEET_COUNT = 50;
    private static TickerManager singleton;
    private static int tweetIndex = 0;

    private TickerManager() {
    }

    public static TickerManager getInstance() {
        if (singleton == null) {
            singleton = new TickerManager();
        }
        return singleton;

    }

    public void init() {
        ScheduledExecutorService tweetService = Executors
                .newScheduledThreadPool(1);
        tweetService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                beat();
            }
        }, 0, HEARTBEAT_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    private void beat() {
        final StringWriter tweetContents = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(tweetContents);
        Scanner sc = new Scanner(getClass().getResourceAsStream("tweets"));
        int line = 0;

        while (line < tweetIndex) {
            sc.nextLine();
            line++;
        }

        try {
            bufferedWriter.write(sc.nextLine());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TwitterManager.getInstance().tweet(tweetContents.toString());

        tweetIndex++;
        tweetIndex %= TWEET_COUNT;
    }
}
