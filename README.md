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


# Configuration

The application can be configured using a custom properties file. It can be placed anywhere visible to the application and may have any name.

The available properties are:

### Database

DOP uses **MariaDB** database. Click [here](https://mariadb.com/kb/en/mariadb/getting-installing-and-upgrading-mariadb/) for how to install it.

* **db.url** - the database url
* **db.username** - the database username
* **db.password** - the database password for given username 

### Server

* **server.port** - the port that server starts.
* **command.listener.port** - the port used to listening for commands to be executed on server. Currently only shutdown command is available.

### Search Engine

* **search.server** - the URL for the search engine. Currently only Solr is supported. Default value is **http://localhost:8983/solr/dop/**