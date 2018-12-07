package net.unicon.carbon.user.ldap;

import groovy.lang.GroovyObject;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jgasper on 10/13/16.
 */
public class ScriptedAttributeInjectorHelperTest extends TestCase {

    public void testPopulateScriptsFromMissingFile() throws Exception {
        System.out.println("Ignore the following 'Fatal Error'; it is expected.");
        Map scripts = ScriptedAttributeInjectorHelper.populateScriptsFromFile("");
        assertEquals(0, scripts.size());
    }

    public void testPopulateScriptsFromEmptyFile() throws Exception {
        System.out.println("Ignore the following 'Fatal Error'; it is expected.");
        Map scripts = ScriptedAttributeInjectorHelper.populateScriptsFromFile("src/test/resources/empty.xml");
        assertEquals(0, scripts.size());
    }

    public void testPopulateScriptsFromStandardFile() throws Exception {
        Map scripts = ScriptedAttributeInjectorHelper.populateScriptsFromFile("src/test/resources/adusmsai.xml");
        assertEquals(2, scripts.size());
        assertNotNull(scripts.get("src/test/resources/test.groovy"));
    }


    public void testInjectScriptedAttributes() throws Exception {
        //staticAttributeMapping
        final String test1 = "test";
        final String testValue1 = "testValue";

        final String test2 = "test2";
        final String testValue2a = "testValueA";
        final String testValue2b = "testValueB";

        Map<String, List<String>> staticAttributeMapping = new HashMap<>();
        staticAttributeMapping.put(test1, new ArrayList<String>());
        staticAttributeMapping.get(test1).add(testValue1);
        staticAttributeMapping.put(test2, new ArrayList<String>());
        staticAttributeMapping.get(test2).add(testValue2a);

        //propertyNames
        String[] propertyNames = new String[] { test1, test2 };

        //populatedProperties
        Map<String, String> populatedProperties = new HashMap<>();
        populatedProperties.put(test1, testValue1);
        populatedProperties.put(test2, testValue2a + "," + testValue2b);

        //expectedPopulatedProperties
        Map<String, String> expectedPopulatedProperties = new HashMap<>();
        expectedPopulatedProperties.put(test1, testValue1);
        expectedPopulatedProperties.put(test2, testValue2a + "," + testValue2b);
        expectedPopulatedProperties.put("groovy", "test");
        expectedPopulatedProperties.put("groovy2", "testValuetestValue");
        expectedPopulatedProperties.put("o365_mail", "testValue");

        //Load up a test script.
        Map<String, GroovyObject> scriptedAttributeMapping = ScriptedAttributeInjectorHelper.populateScriptsFromFile("src/test/resources/adusmsai.xml");

        //Test
        ScriptedAttributeInjectorHelper.injectScriptedAttributes(propertyNames, scriptedAttributeMapping, populatedProperties);

        //Verification
        assertEquals(expectedPopulatedProperties.size(), populatedProperties.size());

        for (String item : populatedProperties.keySet()) {
            assertEquals(item, expectedPopulatedProperties.get(item), populatedProperties.get(item));
        }
    }


}