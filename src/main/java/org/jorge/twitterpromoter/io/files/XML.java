package org.jorge.twitterpromoter.io.files;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static final Path ENTRIES_FILE = Paths.get("entries.xml");
    private static final String ENTRY_TYPE = "entry", CONTENT_ATTRIBUTE = "contents", ID_ATTRIBUTE = "id";
    private static int ID_COUNTER = 0;

    public static void addEntry(String entryContent) {
        ArrayList<String[]> attr = new ArrayList<>();
        String[] attributeContent = new String[2];
        attributeContent[0] = CONTENT_ATTRIBUTE;
        attributeContent[1] = entryContent;
        attr.add(attributeContent);
        addNode(ENTRY_TYPE, ID_ATTRIBUTE, ID_COUNTER + "", ENTRIES_FILE, attr);
        ID_COUNTER++;
    }

    /**
     * Adds a new node to a file. If the file does not yet exists, it is created before the addition is performed.
     *
     * @param nodeType            {@link String} The type of the element to add.
     * @param idField             {@link String} The name of the field used to identify this
     *                            node.
     * @param nodeID              {@link String} The identifier for this node, so its data
     *                            can be later retrieved and modified.
     * @param destinationFilePath {@link Path} The file where the node must be added.
     * @param attributes          {@link ArrayList} of array of String. The arrays must
     *                            be bi-dimensional (first index must contain attribute name, second one
     *                            attribute value). Otherwise, an error will be thrown. However, if
     *                            <value>null</value>, it is ignored.
     */
    private static void addNode(String nodeType, String idField, String nodeID, Path destinationFilePath,
                                ArrayList<String[]> attributes) {
        if (!destinationFilePath.toFile().exists()) {
            createEntriesFile(destinationFilePath);
        }
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
            doc = docBuilder.parse(destinationFilePath.toFile());
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
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destinationFilePath.toFile()))) {
            bufferedWriter.write(xmlString);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        /*
         * XML DATA DUMP - END
         */
    }

    public static boolean containsEntry(String entry) {
        return getAllEntries().contains(entry);
    }

    private static void createEntriesFile(Path destinationFilePath) {
        String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root></root>";
        try {
            Files.createFile(destinationFilePath);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(destinationFilePath.toString(),
                Boolean.FALSE))) {
            pw.write(contents);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Parses a XML file and retrieves the requested attribute of a certain XML
     * node.
     *
     * @param type                {@link String} The type identifier of the XML node.
     * @param identifierAttribute {@link String} The name of the attribute that
     *                            identifies the node.
     * @param identifierValue     {@link String} The value that the identifier
     *                            attribute will have in the requested node so it can be found.
     * @param attributeName       {@link String} The name of the attribute to
     *                            retrieve.
     * @param destinationFilePath {@link Path} The file containing the data where the
     *                            information must be retrieved from.
     * @return The value of the requested attribute for the requested node.
     */
    public static String getAttribute(String type, String identifierAttribute, String identifierValue,
                                      String attributeName, Path destinationFilePath) {
        DocumentBuilder docBuilder;
        Document doc;

        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.parse(destinationFilePath.toFile());
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            return null;
        }

        return XML.getAttribute(type, identifierAttribute, identifierValue, attributeName, doc);
    }

    /**
     * Parses a XML file and retrieves the requested attribute of a certain XML
     * node.
     *
     * @param type                {@link String} The type identifier of the XML node.
     * @param identifierAttribute {@link String} The name of the attribute that
     *                            identifies the node.
     * @param identifierValue     {@link String} The value that the identifier
     *                            attribute will have in the requested node so it can be found.
     * @param attributeName       {@link String} The name of the attribute to
     *                            retrieve.
     * @param doc                 {@link Document} The document containing the data where the
     *                            information must be retrieved from.
     * @return The value of the requested attribute for the requested node.
     */
    public static String getAttribute(String type, String identifierAttribute, String identifierValue,
                                      String attributeName, Document doc) {
        NodeList parent = doc.getElementsByTagName(type);

        for (int i = 0; i < parent.getLength(); i++) {
            if (((Element) parent.item(i)).getAttribute(identifierAttribute).matches(identifierValue)) {
                return ((Element) parent.item(i)).getAttribute(attributeName);
            }
        }

        return null;
    }

    private static ArrayList<String> getAllEntries() {
        ArrayList<String> ids = getAllOfType(ENTRY_TYPE, ID_ATTRIBUTE, ENTRIES_FILE), ret = new ArrayList<>();
        for (String id : ids) {
            ret.add(getAttribute(ENTRY_TYPE, ID_ATTRIBUTE, id, CONTENT_ATTRIBUTE, ENTRIES_FILE));
        }

        return ret;
    }

    /**
     * Parses a XML file and retrieves the identifier attribute of all nodes of
     * a given type.
     *
     * @param type                {@link String} The type identifier.
     * @param identifierAttribute {@link String} The name of the attribute that
     *                            identifies the node.
     * @param destinationFilePath {@link Path} The file containing the data where the
     *                            information must be retrieved from.
     * @return The value of the requested attribute for the requested node.
     */
    private static ArrayList<String> getAllOfType(String type, String identifierAttribute, Path destinationFilePath) {
        ArrayList<String> ret = new ArrayList<>();
        DocumentBuilder docBuilder;
        Document doc;

        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docBuilder.parse(destinationFilePath.toFile());
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            return null;
        }

        NodeList parent = doc.getElementsByTagName(type);

        for (int i = 0; i < parent.getLength(); i++) {
            ret.add(((Element) parent.item(i)).getAttribute(identifierAttribute));
        }

        return ret;
    }
}
