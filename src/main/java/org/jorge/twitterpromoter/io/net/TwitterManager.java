package org.jorge.twitterpromoter.io.net;

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

    private static final String TOO_LONG_APPENDIX = "... ", SOURCE_URL = "YOUR_SOURCE_URL_HERE", ACCESS_TOKEN = "YOUR_ACCESS_TOKEN_HERE",
            ACCESS_TOKEN_SECRET = "YOUR_ACCESS_TOKEN_SECRET", CONSUMER_KEY = "YOUR_CONSUMER_KEY", CONSUMER_SECRET = "YOUR_CONSUMER_SECRET", USER_NAME = "YOUR_USER_NAME_HERE", APP_NAME = "YOUR_APP_NAME", APP_KEY =
            "YOUR_APP_KEY_HERE";
    private static final int TWEET_LENGTH_LIMIT = 140;

    private static TwitterManager singleton;

    private TwitterManager() {
        if (SOURCE_URL.length() + TOO_LONG_APPENDIX.length() > TWEET_LENGTH_LIMIT) {
            throw new IllegalArgumentException(
                    "Source url too long, can't be shown in the tweets (what about using bit.ly or such?)");
        }
    }

    public static TwitterManager getInstance() {
        if (singleton == null) {
            singleton = new TwitterManager();
        }
        return singleton;
    }

    public void tweet(String entry) {
        //TODO
    }
}
