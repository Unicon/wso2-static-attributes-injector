/*
 * Copyright 2016 Unicon, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.unicon.carbon.user.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class
 */
public class StaticAttributeInjectorHelper {

    /** logger object */
    private static Log log = LogFactory.getLog(StaticAttributeInjectorHelper.class);

    /**
     * populates a static attribute mapping
     * @param file the input xml file
     * @return the mapping
     */
    public static Map<String, List<String>> populateMappingFromFile(String file) {
        Map<String, List<String>> mapping = new HashMap<>();

        try {
            File xmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList attributeNodeList = doc.getElementsByTagName("attribute");

            for (int i = 0; i < attributeNodeList.getLength(); i++) {
                Node attributeNode = attributeNodeList.item(i);

                if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) attributeNode;

                    String attributeName = eElement.getAttribute("name");
                    log.debug("attributeName : " + attributeName);

                    NodeList attributeValueNodeList = eElement.getElementsByTagName("value");
                    List<String> attributeValueList = new ArrayList<>();

                    for (int j = 0; j < attributeValueNodeList.getLength(); j++) {
                        Node attributeValueNode = attributeValueNodeList.item(j);
                        Element attributeValueElement = (Element) attributeValueNode;

                        if (attributeValueNode.getNodeType() == Node.ELEMENT_NODE) {
                            String attributeValue = attributeValueElement.getTextContent();
                            log.debug("attributeValue : " + attributeValue);
                            attributeValueList.add(attributeValue);
                        }
                    }

                    if (attributeValueList.size() > 0) {
                        mapping.put(attributeName, attributeValueList);
                    }
                }
            }

        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }

        return mapping;
    }

    /**
     * inject the static attribute mappings if requested.
     * @param propertyNames a list of requested properties
     * @param staticAttributeMapping the attribute mapping loaded by the injector-supported UserStoreManager
     * @param populatedProperties the updated populateProperties list
     */
    public static void injectMappedAttributes(String[] propertyNames, Map<String, List<String>> staticAttributeMapping, Map<String, String> populatedProperties) {
        for (String propertyName : propertyNames) {
            if (staticAttributeMapping.containsKey(propertyName)) {
                List<String> staticAttribute =  staticAttributeMapping.get(propertyName);

                if (staticAttribute.size() == 1) {
                    String value = staticAttribute.get(0);
                    log.debug("sending single value of " + value + " for " + propertyName);
                    populatedProperties.put(propertyName, staticAttribute.get(0));

                } else if (staticAttribute.size() > 1) {
                    StringBuilder multivaluedAttributes = new StringBuilder();

                    for (String value : staticAttribute) {
                        multivaluedAttributes.append(value).append(",");
                    }

                    //Send the MVA without the dangling comma.
                    String value = multivaluedAttributes.toString().replaceAll(",$", "");
                    log.debug("sending multi-values of " + value + " for " + propertyName);
                    populatedProperties.put(propertyName, value);
                }
            }
        }
    }
}
