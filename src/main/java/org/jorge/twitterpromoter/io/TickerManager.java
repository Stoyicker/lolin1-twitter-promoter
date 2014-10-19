package org.jorge.twitterpromoter.io;

import org.jorge.twitterpromoter.io.net.TwitterManager;

import java.io.*;
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
        System.out.println("Beat");
        Scanner sc;
        try {
            sc = new Scanner(new FileInputStream("tweets"));
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
            return;
        }
        final StringWriter tweetContents = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(tweetContents);
        int line = 0;

        System.out.println("Looking for line " + tweetIndex);
        while (line < tweetIndex) {
            sc.nextLine();
            line++;
        }

        System.out.println("About to write in the buffer");
        try {
            bufferedWriter.write(sc.nextLine());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (TwitterManager.getInstance().tweet(tweetContents.toString())) {
            tweetIndex++;
            tweetIndex %= TWEET_COUNT;
        }
    }
}
