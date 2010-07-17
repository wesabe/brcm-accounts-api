BRCM Accounts API
=================

How to work on this project
---------------------------

1.  Install and configure Maven2:
    
        sudo port install maven2
    
    Be sure to set the JAVA_HOME environment variable:
    
        export JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home"
    
    You'll know it's working when you get something like this:
    
        $ mvn -v
        Apache Maven 2.2.0 (r788681; 2009-06-26 06:04:01-0700)
        Java version: 1.6.0_15
        Java home: /System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home
        Default locale: en_US, platform encoding: MacRoman
        OS name: "mac os x" version: "10.6.1" arch: "x86_64" Family: "mac"
    
2.  Download and install Eclipse:
    
        http://www.eclipse.org
    
    (Eclipse 3.4 for Java SE is recommended, but you can use anything which
    supports the following plugins.)
    
    * Set the default JDK to 6.0 (Preferences > Java > Installed JREs)
    * Set the compiler to Java 1.6 (Preferences > Java > Compiler)
    * Set the default charset to UTF-8 (Preferences > General > Workspace)

3.  Install the Maven Eclipse plugin:
    
        http://m2eclipse.codehaus.org/
    
    Install Maven Integration for Eclipse, Maven POM Editor, Maven POM XML
    Editor.
    
    * Set Maven to download sources, if possible (Preferences > Maven)

4.  Check out the project.

5.  Make sure you have a working config file, `development.properties`, in the
    project directory. It should look something like this:
    
        hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
        hibernate.connection.username=pfc
        hibernate.connection.password=woo
        hibernate.connection.url=jdbc:mysql://localhost:3306/pfc_development?zeroDateTimeBehavior=convertToNull
    
    **N.B.:** The `?zeroDateTimeBehavior=convertToNull` is pretty important--it
    keeps MySQL's JDBC drivers from exploding when it encounters a date like 
    `0000-00-00 00:00:00` (which technically shouldn't exist, but does in our
    database).
    
    If you don't know the username, password, or database name for your local   
    MySQL database, check the `config/database.yml` file for your checkout of 
    PFC.

6.  Run the tests:
    
        rake test

7.  Run the Maven Eclipse task to generate an Eclipse project:

        mvn eclipse:eclipse

8. Import the Eclipse project into your Eclipse workspace (File > Import).


Running BRCM
------------

1. Add to your /etc/hosts file:

        127.0.0.1  services.local

2. rake run

3. If you get an out-of-memory error try setting your MAVEN_OPTS environment 
   variable to `-Xmx512m -XX:MaxPermSize=256m`.


For Unsupervised Operations
---------------------------

If you're setting BRCM up to run on the Wesabe servers, be sure to configure
that user's Maven to use the proxy:

Edit `~/.m2/settings.xml`:

    <settings>
      <proxies>
       <proxy>
          <active>true</active>
          <protocol>http</protocol>
          <host>proxy.oak.wesabe.com</host>
          <port>8080</port>
        </proxy>
      </proxies>
    </settings>

Deploying BRCM
--------------

To deploy BRCM, you will need:

* Capistrano (`gem install capistrano`)
* Grit (`gem install grit`)

Make sure you have net-ssh 2.0.14 or greater, otherwise deploying to multiple
servers will hang.

To deploy BRCM to the staging environment:

    cap staging deploy

To deploy BRCM to the production environment:
    
    cap production deploy

To clean up the old installed versions of BRCM:

    cap production clean

etc.