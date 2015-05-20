To build the project you need to have installed on your machine NodeJS, NPM, Bower, Grunt, Ruby and SASS.

All commands in this page are for Ubuntu 14.10

Download project dependencies

	npm install
	bower install

Those commands will download all necessary packages.

Build

Run from project root directory:

	grunt build
	
This command generates the project artifacts.

Package

To generate project deliverable artifact run this command from project root directory:

	grunt package
	
This command will build the project (grunt build) and create dop.tar.gz file which contains the project artifacts.

Integrate front end with back end

To have and usable application is needed to connect to back end server. For that we use Apache2.

Install Apache2

	sudo apt-get install apache2
	
Install proxy http module to redirect rest calls to back end servlet

	sudo a2enmod proxy_http
	
Configuring Apache

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
    </Directory>

Configure proxy to forward rest request to Back End servlet

	ProxyRequests Off
	ProxyPreserveHost On

	ProxyPass /rest http://127.0.0.1:8080/rest
	ProxyPassReverse /rest http://127.0.0.1:8080/rest

Now you should be able to run the whole application. Make sure to change Back End address to the one you are using, if not localhost. 