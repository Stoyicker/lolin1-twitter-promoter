package org.jorge.twitterpromoter.io.net;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
public final class TwitterManager {

    private static final String ACCESS_TOKEN = System.getenv("ACCESS_TOKEN"),
            ACCESS_TOKEN_SECRET = System.getenv("ACCESS_TOKEN_SECRET"),
            CONSUMER_KEY = System.getenv("CONSUMER_KEY"),
            CONSUMER_SECRET = System.getenv("CONSUMER_SECRET");

    private static TwitterManager singleton;

    private TwitterManager() {
    }

    public static TwitterManager getInstance() {
        if (singleton == null) {
            singleton = new TwitterManager();
        }
        return singleton;
    }

    public Boolean tweet(String message) {
        Twitter twitter = new TwitterFactory(
                new ConfigurationBuilder().setDebugEnabled(true)
                        .setOAuthConsumerKey(CONSUMER_KEY)
                        .setOAuthConsumerSecret(CONSUMER_SECRET)
                        .setOAuthAccessToken(ACCESS_TOKEN)
                        .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET).build()).getInstance();
        try {
            System.out.println("About to send tweet: " + message);
            twitter.updateStatus(message);
            System.out.println("Tweet sent: " + message);
        } catch (TwitterException e) {
            e.printStackTrace(System.err);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
