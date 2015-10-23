# eKoolikott

# Installation

Check out the project and build the application using **maven** and run the dop.jar at **/target** folder.

	mvn clean package

# Start application

By default, application will run at port 8080. The rest API is available under context path **rest/**. After starting application you can access it at:

	http://localhost:8080/rest/path/to/resource

## Default configuration

	java -jar dop.jar
	
## Custom configuration

The custom configuration file can be placed anywhere visible to the application and may have any name.

	java -jar -Dconfig="/path/to/custom.properties" dop.jar

# Stop application

To stop the application just run the command:

	java -jar dop.jar stop
	
If you started the application using a **custom configuration** file, use the same configuration to stop the application.

	java -jar -Dconfig="/path/to/custom.properties" dop.jar stop

# Search engine

The application uses Solr as search engine and Solr must be available and configured.

To install Solr in the production environment see
[Taking Solr to Production](https://cwiki.apache.org/confluence/display/solr/Taking+Solr+to+Production "Solr installation guide").

To install Solr in the development environment see
[Installing Solr](https://cwiki.apache.org/confluence/display/solr/Installing+Solr "Solr installation guide").

### DOP configuration for Solr

* Copy "dop" folder to  "/path/to/solr-x.x.x/server/solr"
* Create folder "lib" under "dop" folder
* Download and copy database connector to "lib" folder (mysql-connector-java-5.1.35.jar in the time of writing this).

# Configuration

The application can be configured using a custom properties file. It can be placed anywhere visible to the application and may have any name.

The available properties are:

### Database

DOP uses **MariaDB** database. Click [here](https://mariadb.com/kb/en/mariadb/getting-installing-and-upgrading-mariadb/) for how to install it.
In my.ini or my.cnf under [mysqld] add: character-set-server=utf8

* **db.url** - the database url
* **db.username** - the database username
* **db.password** - the database password for given username

### Server

* **server.port** - the port that server starts.
* **command.listener.port** - the port used to listening for commands to be executed on server. Currently only shutdown command is available.

### Search Engine

* **search.server** - the URL for the search engine. Currently only Solr is supported. Default value is **http://localhost:8983/solr/dop/**

### TAAT
* **taat.sso** - the URL of TAAT Single Sign-On Service
* **taat.connectionId** - the Connection ID in [JANUS](https://taeva.taat.edu.ee/module.php/janus/index.php)
* **taat.assertionConsumerServiceIndex** - the Assertion Consumer Service Index in JANUS (default 0)
* **taat.metadata.filepath** TAAT service metadata filepath. Default value is **reos_metadata.xml** (this file is included in the application).
* **taat.metadata.entityID** Entity ID in the metadata. Default value is **https://reos.taat.edu.ee/saml2/idp/metadata.php**

#### KeyStore configuration
This keystore holds credentials used to sign outgoing TAAT authentication requests and to sign the user data that is sent to the material provider.

* **keystore.filename** - name of the keystore file
* **keystore.password** - password of the keystore file
* **keystore.signingEntityID** - alias of the key
* **keystore.signingEntityPassword** - password of the key

#### Mobile ID

* **mobileID.endpoint** - DigiDocService endpoint address. 
* **mobileID.serviceName** - Name of your service, must be agreed upon with Mobile ID service provider. Maximum length 20 characters. 
* **mobileID.namespace.prefix** - Prefix to use in SOAP messages. Default value is **dig**
* **mobileID.namespace.uri** - DigiDocService WSDL URI. Default value is **http://www.sk.ee/DigiDocService/DigiDocService_2_3.wsdl**
* **mobileID.messageToDisplay** - Extra message that can be displayed on user's phone prior to entering the PIN code. Maximum size is 40 bytes. Default value is **eKoolikott**

# Generating the public private key pair in a keystore for DOP

Create a new keystore file:

* A valid certificate is required. A self-signed certificate can be generated with the following commands (don't use self-signed certificates for production environment): 
	* `openssl req -nodes -new -keyout server.pem -newkey rsa:2048 > server.csr`
	* `openssl x509 -req -days 1095 -in server.csr -signkey server.pem -out server.crt`
* Create a pkcs12 file:
	* `openssl pkcs12 -export -in server.crt -inkey server.pem -out server.p12 -name exampleAlias -password pass:examplePassword` Replace **exampleAlias** and **examplePassword**. 
* Create the keystore:
	* `keytool -importkeystore -deststorepass exampleStorePass -destkeypass exampleKeyPass -destkeystore server.keystore -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass examplePassword -alias exampleAlias` Replace **exampleAlias**, **examplePassword**, **exampleStorePass**, **exampleKeyPass**.
	
Add all keystore configurations to **custom.properties**. From the example keytool command the configuration would be: 
```
	keystore.filename=server.keystore
	keystore.password=exampleStorePass
	keystore.signingEntityID=exampleAlias
	keystore.signingEntityPassword=exampleKeyPass
```

### Exporting the generated public key certificate

To export the public key certificate, which can then be given to the material providers (publishers):
	*`keytool -export -keystore server.keystore -alias exampleAlias -file EkoolikottPublicKeyCert.cer`
	
This EkoolikottPublicKeyCert.cer file can then be used by the material providers to verify, if the user should have the access to the required resource.
# TAAT authentication setup

In order to set up TAAT authentication you have to have a keystore set up with a public/private key pair.

Create a new connection in [JANUS](https://taeva.taat.edu.ee/module.php/janus/index.php)

* Insert a **Connection ID**. Read how to choose it [here](https://spaces.internet2.edu/display/InCFederation/Entity+IDs). For example: https&#58;//www.example.com/sp
* Add all required metadata fields and save. Here are some example values:
	
Entry | Value
--- | ---
AssertionConsumerService:0:Binding | urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST
AssertionConsumerService:0:Location | https&#58;//www.example.com/rest/login/taat
SingleLogoutService:0:Binding | urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect
SingleLogoutService:0:Location | https&#58;//www.example.com/rest/logout/taat
certData | MIIkjIdnlJ8Kdj342viHOmd214j8kDj...
url:en | https&#58;//www.example.com
url:et | https&#58;//www.example.com
OrganizationName:en | Example Organization
OrganizationName:et | Example Organization
OrganizationDisplayName:et | Example Organization
OrganizationDisplayName:en | Example Organization
OrganizationURL:en | https&#58;//www.example.com
OrganizationURL:et | https&#58;//www.example.com
NameIDFormat | urn:oasis:names:tc:SAML:2.0:nameid-format:transient
name:en | https&#58;//www.example.com/sp
name:et | https&#58;//www.example.com/sp
	
Add TAAT configurations to **custom.properties**. For example: 
```
	taat.sso=https://reos.taat.edu.ee/saml2/idp/SSOService.php
	taat.connectionId=https://www.example.com/sp
	taat.assertionConsumerServiceIndex=0
```

## Production mode
When your TAAT connection is changed to production status, you can set
```
	taat.sso=https://sarvik.taat.edu.ee/saml2/idp/SSOService.php
	taat.metadata.entityID=https://sarvik.taat.edu.ee/saml2/idp/metadata.php
```
and download the metadata from the URL and set **taat.metadata.filepath** to point to the filepath. 

# Mobile ID authentication setup

By default **mobileID.endpoint** is set to the Test DigiDocService at `https://www.openxades.org:9443/DigiDocService`. 
Set **mobileID.endpoint** to `https://digidocservice.sk.ee/DigiDocService` to use the real endpoint. 
Set **mobileID.serviceName** to the name that has been agreed upon with the Mobile ID provider. 
