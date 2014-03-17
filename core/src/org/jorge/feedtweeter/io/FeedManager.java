package org.jorge.feedtweeter.io;

import org.jorge.feedtweeter.io.files.XML;
import org.jorge.feedtweeter.io.net.TwitterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This file is part of feed-tweeter.
 * <p/>
 * feed-tweeter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * feed-tweeter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with feed-tweeter. If not, see <http://www.gnu.org/licenses/>.
 * Created by JorgeAntonio on 17/03/14.
 */
public class FeedManager {

    private static final int HEARTBEAT_DELAY_SECONDS = 60 * 10;
    private static final long INTERNET_ERROR_DELAY_MILLIS = 1000 * 60 * 5;
    private static FeedManager singleton;

    private FeedManager() {
    }

    public static FeedManager getInstance() {
        if (singleton == null) {
            singleton = new FeedManager();
        }
        return singleton;

    }

    private void beat() {
        ArrayList<String> entries;
        try {
            entries = org.jorge.feedtweeter.io.net.FeedManager.getInstance().readFeed();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            try {
                Thread.sleep(INTERNET_ERROR_DELAY_MILLIS);
            } catch (InterruptedException e1) {
                e1.printStackTrace(System.err);
            }
            beat();
            return;
        }
        for (String entry : entries) {
            if (!exists(entry)) {
                TwitterManager.getInstance().tweet(entry);
            }
        }
    }

    private boolean exists(String entry) {
        return XML.containsEntry(entry);
    }

    public void init() {
        ScheduledExecutorService updateService = Executors
                .newScheduledThreadPool(1);
        updateService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                beat();
            }
        }, 0, HEARTBEAT_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    public void addEntry(String entry) {
        XML.addEntry(entry);
    }
}
