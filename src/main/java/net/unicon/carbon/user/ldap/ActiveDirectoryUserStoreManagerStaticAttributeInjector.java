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
import org.wso2.carbon.user.core.claim.ClaimManager;
import org.wso2.carbon.user.core.ldap.ActiveDirectoryUserStoreManager;
import org.wso2.carbon.user.core.profile.ProfileConfigurationManager;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;

import java.util.List;
import java.util.Map;

/**
 * ActiveDirectoryUserStoreManagerStaticAttributeInjector allows static attributes to be injected into the results of an ActiveDirectoryUserStoreManager.
 */
public class ActiveDirectoryUserStoreManagerStaticAttributeInjector extends ActiveDirectoryUserStoreManager {

    /** logger object */
    private static Log log = LogFactory.getLog(ActiveDirectoryUserStoreManagerStaticAttributeInjector.class);

    /** mapping of static attributes */
    private Map<String, List<String>> staticAttributes;

    /**
     * @see ActiveDirectoryUserStoreManager(RealmConfiguration, Map, ClaimManager, ProfileConfigurationManager, UserRealm, Integer)
     */
    public ActiveDirectoryUserStoreManagerStaticAttributeInjector(RealmConfiguration realmConfig, Map<String, Object> properties,
            ClaimManager claimManager, ProfileConfigurationManager profileManager, UserRealm realm, Integer tenantId)
            throws UserStoreException {

        super(realmConfig, properties, claimManager, profileManager, realm, tenantId);

        log.debug("ActiveDirectoryUserStoreManagerStaticAttributeInjector initializing...");
        staticAttributes = StaticAttributeInjectorHelper.populateMappingFromFile("repository/conf/adusmsai.xml");;
        log.info("ActiveDirectoryUserStoreManagerStaticAttributeInjector initialized");
    }

    /**
     * Adds in additional static properties/values after AD/LDAP has been queried.
     * @param userName the user to retrieve attributes for
     * @param propertyNames properties to retrieve
     * @param profileName  "default"
     * @return populated property list
     * @throws UserStoreException UserStoreException
     */
    @Override
    public Map<String, String> getUserPropertyValues(String userName, String[] propertyNames, String profileName) throws UserStoreException {
        Map<String, String> populatedProperties = super.getUserPropertyValues(userName, propertyNames, profileName);
        StaticAttributeInjectorHelper.injectMappedAttributes(propertyNames, staticAttributes, populatedProperties);

        return populatedProperties;
    }
}
