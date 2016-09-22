# Tools

To build the project you need to have installed on your machine NodeJS, NPM, Bower, Grunt, Ruby and SASS.

## Bower installation

	npm install -g bower
	
## Grunt installation

        npm install -g grunt-cli

## Compass installation (depends on ruby)

        sudo apt-get install ruby-full
        sudo gem install compass

# Build

Run commands from project root directory.

## Download project dependencies

	npm install
	bower install

## Compile sass files to css

    grunt compass

## Building

Generates project artifacts.

	grunt build

If project building fails with message like ">> Local Npm module "XXXXXX" not found. Is it installed?" then run command

    npm install

## Package

Generates project deliverable artifact. builds the project (grunt build) and create kott.tar.gz file which contains the project artifacts.

	grunt package
	
## Visual regression testing

    sudo apt-get install xvfb
    sudo apt-get install graphicsmagick
    
##  Run visual regression tests

    grunt regression-test

# Front and Back end integration

To have and usable application is needed to connect to back end server. For that we use Apache2. All commands in this page are for Ubuntu 14.10

## Installing Apache2

	sudo apt-get install apache2
	
## Installing proxy http module to redirect rest calls to back end servlet

	sudo a2enmod proxy_http
	
## Installing ssl https module to allow ssl connections

	sudo a2enmod ssl
	
## Installing rewrite module to allow url rewriting

	sudo a2enmod rewrite
	
## Configuring Apache
### Serving project files

To have the ability to develop Front End without copying files to Apache2 directory every time you change something, we have to configure DocumentRoot to point to project root directory.

Edit 000-default.conf

	 sudo nano /etc/apache2/sites-available/000-default.conf

Change DocumentRoot and make sure Apache has rights to read the target folder.

	DocumentRoot /path/to/project
	
Also add those lines to the configuration just bellow DocumentRoot
		
	<Directory />
        Require all granted
        Options Indexes FollowSymLinks Includes ExecCGI
        AllowOverride All
        Order deny,allow
        Allow from all
        
        RewriteEngine on

		 # Don't rewrite files or directories
		 RewriteCond %{REQUEST_FILENAME} -f [OR]
		 RewriteCond %{REQUEST_FILENAME} -d
		 RewriteRule ^ - [L]

		 # Rewrite everything else to index.html to allow html5 state links
		 RewriteRule ^ index.html [L]
    </Directory>

### Proxy configuration

Configure proxy to forward rest request to Back End servlet

	ProxyRequests Off
	ProxyPreserveHost On

	ProxyPass /rest http://127.0.0.1:8080/rest
	ProxyPassReverse /rest http://127.0.0.1:8080/rest

You should be able to run the whole application. Make sure to change Back End address to the one you are using, if not localhost. 

### HTTPS Configuration 

Refer to apache documentation to configure https. 

All requests http must be redirected to https. The mod_alias module needs to be enabled.

	<VirtualHost *:80>
        Redirect permanent / https://yoursite.com/
	</VirtualHost>

### Id card configuration

To configure id card refer to [Configuring Apache to support ID-card](http://www.id.ee/public/Configuring_Apache_web_server_to_support_ID.pdf).

Id card configuration.

	SSLCertificateFile /yourpath/certs/server.crt
	SSLCertificateKeyFile /yourpath/certs/server.key
	SSLCACertificateFile  /yourpath/certs/id.crt
	SSLCARevocationPath /yourpath/revocation/
 	<Location "/rest/login/idCard">
        #verify if user was authenticated
        RequestHeader set SSL_AUTH_VERIFY ""
        RequestHeader set SSL_AUTH_VERIFY "%{SSL_CLIENT_VERIFY}s"

        # put user info (name, idcode, etc) from the id card to header
        RequestHeader set SSL_CLIENT_S_DN ""
        RequestHeader set SSL_CLIENT_S_DN "%{SSL_CLIENT_S_DN}s"

        SSLVerifyClient require
        SSLVerifyDepth  2
 	</Location>


### An example configuration file for Apache

 	<VirtualHost *:80>
        Redirect permanent / https://yoursite.com/
 	</VirtualHost>

 	<VirtualHost *:443>
        ServerName yourserver.com
        DocumentRoot /path/to/project

        ProxyRequests Off
        ProxyPreserveHost On

        ErrorLog ${APACHE_LOG_DIR}/error.log
        CustomLog ${APACHE_LOG_DIR}/access.log combined

        ProxyPass /rest http://yourserver.com:8080/rest
        ProxyPassReverse /rest http://yourserver.com:8080/rest

        SSLEngine on
        SSLCertificateFile /yourpath/certs/server.crt
        SSLCertificateKeyFile /yourpath/certs/server.key
        SSLCACertificateFile  /yourpath/certs/id.crt
        SSLCARevocationPath /yourpath/revocation/

        <Location "/rest/login/idCard">

          #verify if user was authenticated
          RequestHeader set SSL_AUTH_VERIFY ""
          RequestHeader set SSL_AUTH_VERIFY "%{SSL_CLIENT_VERIFY}s"

          # put user info (name, idcode, etc) from the id card to header
          RequestHeader set SSL_CLIENT_S_DN ""
          RequestHeader set SSL_CLIENT_S_DN "%{SSL_CLIENT_S_DN}s"

          SSLVerifyClient require
          SSLVerifyDepth  2
         </Location>
 	</VirtualHost>

	
