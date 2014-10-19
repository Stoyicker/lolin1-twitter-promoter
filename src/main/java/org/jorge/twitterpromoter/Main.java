package org.jorge.twitterpromoter;

import org.jorge.twitterpromoter.io.TickerManager;

public class Main {

    /**
     * @param args The args to the main method
     */
    public static void main(String[] args) throws Exception {

        Thread dummyThreadThatWillNeverStart = new Thread();

        TickerManager.getInstance().init();

        dummyThreadThatWillNeverStart.join();
    }
}
