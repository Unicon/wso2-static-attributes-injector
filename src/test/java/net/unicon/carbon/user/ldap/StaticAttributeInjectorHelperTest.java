package net.unicon.carbon.user.ldap;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jgasper on 10/13/16.
 */
public class StaticAttributeInjectorHelperTest extends TestCase {

    public void testPopulateMappingFromMissingFile() throws Exception {
        System.out.println("Ignore the following 'Fatal Error'; it is expected.");
        Map mapping = StaticAttributeInjectorHelper.populateMappingFromFile("");
        assertEquals(0, mapping.size());
    }

    public void testPopulateMappingFromEmptyFile() throws Exception {
        System.out.println("Ignore the following 'Fatal Error'; it is expected.");
        Map mapping = StaticAttributeInjectorHelper.populateMappingFromFile("src/test/resources/empty.xml");
        assertEquals(0, mapping.size());
    }

    public void testPopulateMappingFromStandardFile() throws Exception {
        Map<String, List<String>> mapping = StaticAttributeInjectorHelper.populateMappingFromFile("src/test/resources/adusmsai.xml");
        assertEquals(2, mapping.size());
        assertEquals("123456", mapping.get("schoolCode").get(0));
        assertTrue("member", mapping.get("eduPersonAffiliation").contains("member"));
        assertTrue("student", mapping.get("eduPersonAffiliation").contains("student"));
    }

    public void testInjectMappedAttributes() throws Exception {
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
        staticAttributeMapping.get(test2).add(testValue2b);

        //propertyNames
        String[] propertyNames = new String[] {test1, test2};

        //populatedProperties
        Map<String, String> populatedProperties = new HashMap<>();

        //expectedPopulatedProperties
        Map<String, String> expectedPopulatedProperties = new HashMap<>();
        expectedPopulatedProperties.put(test1, testValue1);
        expectedPopulatedProperties.put(test2, testValue2a + "," + testValue2b);

        //Test
        StaticAttributeInjectorHelper.injectMappedAttributes(propertyNames, staticAttributeMapping, populatedProperties);

        //Verification
        assertEquals(expectedPopulatedProperties.size(), populatedProperties.size());

        for (String item : populatedProperties.keySet()) {
            assertEquals(item, expectedPopulatedProperties.get(item), populatedProperties.get(item));
        }
    }

}