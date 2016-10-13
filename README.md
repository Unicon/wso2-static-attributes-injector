WSO2 Static Claims Injector
===========================

The WSO2 Static Claims Injector supports a short coming in WSO2 Identity Server (IS) in that it can't support hardcoded (i.e. static) attributes that can apply to all users.
This plugin allows an IS server admin to specify user properties (attributes) and values that will be applied to all users. This can be handy when a service provider
needs organization specific information.  

> This plugin is designed to work with WSO2 Identity Server 4.2/Ellucian Identity Service 1.1.
 
## Installation Instruction

The following steps are needed to apply this plugin:

1. Build the jar using `mvn clean package`.
2. Copy `wso2-static-claims-injector-<VERSION>.jar` to `<EIS-CARBON_HOME>/repository/components/lib/`.
3. Copy `adusmsai.xml` to `<EIS-CARBON_HOME>/repository/conf/`.
4. Update `adusmsai.xml` as described below.
5. Update ...
6. Restart the service.


## `adusmsai.xml` file description and format

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

## Other UserStoreManager cases
It should be trivial to clone the `ActiveDirectoryUserStoreManagerStaticAttributeInjector` class so that its logic can support other UserStoreManager implementations.