WSO2 Static Attributes Injector
===============================

The WSO2 Static Attributes Injector supports a short coming in WSO2 Identity Server (IS)/Ellucian Identity Services (EIS) in that it
can't support hardcoded (i.e. static) attributes/claims that apply to all users. It also supports writing Groovy scripts that can 
manipulate the claims as they are retrieved from Active Directory (AD) and stored in the user's claims "bag".
This plugin allows an IS server admin to specify user properties (attributes) and values that will be applied to all users. 
This can be handy when a service provider needs organization specific information or dynamically generated/calculated information.  

> This plugin is designed to work with WSO2 Identity Server 4.2/Ellucian Identity Service 1.1.
 
## Installation Instruction

The following steps are needed to apply this plugin:

1. Build the jar using `mvn clean package`.
2. Copy `wso2-static-attributes-injector-<VERSION>.jar` to `<EIS-CARBON_HOME>/repository/components/lib/`.
3. Update `<EIS-CARBON_HOME>/repository/conf/user-mgt.xml` as described below.
4. Copy `adusmsai.xml` to `<EIS-CARBON_HOME>/repository/conf/`.
5. Update `adusmsai.xml` as described below.
6. Restart the service.
7. Log into the WSO2 console and define the static and scripted attributes as WSO2 claims (marked them as read-only).
    > Claims used by the scripted attributes code also need to be defined so that the claims/values are pulled in from AD. 

## `user-mgt.xml` Settings
Update `user-mgt.xml` to utilize the `ActiveDirectoryUserStoreManagerStaticAttributeInjector` class instead of 
`ActiveDirectoryUserStoreManager` by changing:
 
```
<UserStoreManager class="org.wso2.carbon.user.core.ldap.ActiveDirectoryUserStoreManager">
```

to:

```
<UserStoreManager class="net.unicon.carbon.user.ldap.ActiveDirectoryUserStoreManagerStaticAttributeInjector">
```

## `adusmsai.xml` File Description and Format

`adusmsai.xml` is used to instruct the plugin as to which attributes and values should be injected into the user profile when IS query user properties.

The following example provides two attributes (schoolCode and eduPersonAffiliation) and their respective values that all users will receive regardless of what the AD/LDAP server indicates:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<staticAttributes>
    <attribute name="schoolCode">
        <value>123456</value>
    </attribute>
    <attribute name="eduPersonAffiliation">
        <value>member</value>
        <value>student</value>
    </attribute>
</staticAttributes>
```


### Scripted Attribute Examples

Building upon the previous example, using the script tag will cause WSO2 to import the Groovy scripts and dynamically compile them. The following example reads in two script files

```xml
<?xml version="1.0" encoding="UTF-8"?>
<staticAttributes>
    <attribute name="schoolCode">
        <value>123456</value>
    </attribute>
    <attribute name="eduPersonAffiliation">
        <value>member</value>
        <value>student</value>
    </attribute>
    <script src="repository/conf/script1.groovy"/>
    <script src="repository/conf/script2.groovy"/>
</staticAttributes>
```

Scripts should mimic the following format:

```groovy
class Test {
    void update(Map<String, String> populatedProperties) {
        populatedProperties["groovy"] = "test"

        populatedProperties["groovy2"] = populatedProperties["test1"] + populatedProperties["test2"]
    }
}
```

This example creates two new properties. `groovy` is populated with the value `test`. `groovy2` is populated with the value of the 
`test1` property concatenated with the value of `test2` attribute/property.

Some notes:

1. The class name is arbitrary but should be unique amongst the other scripted attributes.
1. The class is expected to have an update method that matches the above signature.
1. Existing attributes can be read via `populatedProperties["groovy"]`.
1. Attribute can be set via `populatedProperties["groovy2"]`.
1. Any changes made to the populatedProperties object will be returned to WSO2.

## Other UserStoreManager cases
It should be trivial to clone the `ActiveDirectoryUserStoreManagerStaticAttributeInjector` class so that its logic can support other UserStoreManager implementations.
