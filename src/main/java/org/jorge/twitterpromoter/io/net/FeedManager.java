package org.jorge.twitterpromoter.io.net;

import org.jsoup.Jsoup;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

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

    private static final String FEED_URL = "YOUR_FEED_URL_HERE";
    private static FeedManager singleton;

    private FeedManager() {
    }

    public static FeedManager getInstance() {
        if (singleton == null) {
            singleton = new FeedManager();
        }
        return singleton;
    }

    public ArrayList<String> readFeed() throws IOException {
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder feed = new StringBuilder("");
        URL source = new URL(FEED_URL);
        URLConnection urlConnection = source.openConnection();
        urlConnection.connect();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        byte[] contents = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(contents)) != -1) {
            feed.append(new String(contents, 0, bytesRead));
        }
        try {
            ret = processFeed(feed.toString());
        } catch (XPathExpressionException | ParserConfigurationException | SAXException e) {
            e.printStackTrace(System.err);
        }
        Collections.reverse(ret);
        return ret;
    }

    private ArrayList<String> processFeed(String sourceFeedContents)
            throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        ArrayList<String> ret = new ArrayList<>();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        DocumentBuilderFactory domFactory =
                DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(sourceFeedContents.getBytes()));
        NodeList nodes =
                (NodeList) xpath.evaluate("rss/channel/item/description", doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            final String cleanData = Jsoup.parse(nodes.item(i).getTextContent().replaceAll("<p>(.*)", "")).text();
            ret.add(cleanData);
        }
        return ret;
    }
}
