package com.broadsoft.bwcli;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class BwCliQuery {

    private static XPathFactory xPathFactory = XPathFactory.newInstance();
    private static XPath xPath = xPathFactory.newXPath();


    private static String getContextName(Node node) {
        NodeList childNodes = node.getChildNodes();
        Node nameNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals("name")) {
                nameNode = childNode;
                return nameNode.getTextContent();
            }
        }
        return "";
    }

    private static String getCliPath(Node node) {
        if (node == null) return "";

        if (node.getNodeName().equals("context")) {
            String name = getContextName(node);
            return getCliPath(node.getParentNode()) + "/" + name;
        } else {
            return getCliPath(node.getParentNode());
        }
    }

    public static void main(final String[] args) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();

        String xmlFileNames[] = {"nds.xml", "ns.xml", "ps.xml", "xs.xml", "xsp.xml"};

        for (String xmlFileName : xmlFileNames) {
            try {
                InputStream resourceStream = BwCliQuery.class.getResourceAsStream("xml/" + xmlFileName);
                Document doc = builder.parse(resourceStream);

                XPathExpression expr = xPath.compile(
                        "//parameter/name[contains(text(), '" + args[0] + "')]");

                NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node item = nodes.item(j);
                    System.out.println(getCliPath(nodes.item(j)) + "/" + item.getTextContent());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

