package org.jorge.twitterpromoter.io.net;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationContext;
import twitter4j.http.AccessToken;
import twitter4j.http.OAuthAuthorization;

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

    public void tweet(String message) {
        AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        OAuthAuthorization authorization = new OAuthAuthorization(ConfigurationContext.getInstance(), CONSUMER_KEY, CONSUMER_SECRET, accessToken);
        Twitter twitter = new TwitterFactory().getInstance(authorization);
        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            e.printStackTrace(System.err);
        }
    }
}
