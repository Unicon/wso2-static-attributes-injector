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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class
 */
public class ScriptedAttributeInjectorHelper {
    /** logger object */
    private static Log log = LogFactory.getLog(ScriptedAttributeInjectorHelper.class);

    /**
     * populates a static attribute mapping
     * @param file the input xml file
     * @return the mapping
     */
    public static Map<String, GroovyObject> populateScriptsFromFile(String file) {
        Map<String, GroovyObject> scripts = new HashMap<>();

        try {
            File xmlFile = new File(file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList attributeNodeList = doc.getElementsByTagName("script");

            for (int i = 0; i < attributeNodeList.getLength(); i++) {
                Node attributeNode = attributeNodeList.item(i);

                if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) attributeNode;

                    String scriptSrc = eElement.getAttribute("src");
                    log.debug("script source path: " + scriptSrc);

                    GroovyObject go = loadScriptFromSource(scriptSrc);

                    if (go != null ) {
                        scripts.put(scriptSrc, go);
                    }
                }
            }

        } catch(Exception e) {
            log.error(e);
        }

        return scripts;
    }

    /**
     * inject the static attribute mappings if requested.
     * @param propertyNames a list of requested properties
     * @param scriptedAttributes a map of compiled scripted attributes.
     * @param populatedProperties the updated populateProperties list
     */
    public static void injectScriptedAttributes(String[] propertyNames, Map<String, GroovyObject> scriptedAttributes, Map<String, String> populatedProperties) {
        for (String scriptPath : scriptedAttributes.keySet()) {
            log.debug("Running: " + scriptPath);

            try {
                scriptedAttributes.get(scriptPath).invokeMethod("update", new Object[] { populatedProperties });
            } catch (Exception ex) {
                log.error("something bad happened running: " + scriptPath, ex);
            }
        }
    }

    public static GroovyObject loadScriptFromSource(String filepath) {
        log.debug("Processing script: " + filepath);

        final GroovyClassLoader classLoader = new GroovyClassLoader();

        Class groovy = null;
        try {
            groovy = classLoader.parseClass(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GroovyObject groovyObj = null;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }

        return groovyObj;
    }

}
