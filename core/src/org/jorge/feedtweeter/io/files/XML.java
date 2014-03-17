package org.jorge.feedtweeter.io.files;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;

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
public abstract class XML {
    /**
     * Adds a new node to a file.
     *
     * @param nodeType        {@link String} The type of the element to add.
     * @param idField         {@link String} The name of the field used to identify this
     *                        node.
     * @param nodeID          {@link String} The identifier for this node, so its data
     *                        can be later retrieved and modified.
     * @param destinationFile {@link File} The file where the node must be added.
     * @param attributes      {@link ArrayList} of array of String. The arrays must
     *                        be bi-dimensional (first index must contain attribute name, second one
     *                        attribute value). Otherwise, an error will be thrown. However, if
     *                        <value>null</value>, it is ignored.
     */
    public static void addNode(String nodeType, String idField, String nodeID, File destinationFile, ArrayList<String[]> attributes) {
        if (attributes != null) {
            for (String[] attribute : attributes) {
                if (attribute.length != 2) {
                    throw new IllegalArgumentException("Invalid attribute combination");
                }
            }
        }
        /*
         * XML DATA CREATION - BEGINNING
         */
        DocumentBuilder docBuilder;
        Document doc;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.parse(destinationFile);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            return;
        }

        Node index = doc.getFirstChild(), newElement = doc.createElement(nodeType);
        NamedNodeMap elementAttributes = newElement.getAttributes();

        Attr attrID = doc.createAttribute(idField);
        attrID.setValue(nodeID);
        elementAttributes.setNamedItem(attrID);

        if (attributes != null) {
            for (String[] x : attributes) {
                Attr currAttr = doc.createAttribute(x[0]);
                currAttr.setValue(x[1]);
                elementAttributes.setNamedItem(currAttr);
            }
        }

        index.appendChild(newElement);
        /*
         * XML DATA CREATION - END
         */

        /*
         * XML DATA DUMP - BEGINNING
         */
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            return;
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            return;
        }

        String xmlString = result.getWriter().toString();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destinationFile))) {
            bufferedWriter.write(xmlString);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        /*
         * XML DATA DUMP - END
         */
    }
}
