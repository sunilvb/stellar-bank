# Banking on Blockchain with Stellar - Tutorial

This is a tutorial for creating a simple banking application with Stellar.org and Lumen (XLM) as currency.
The following topics are covered in this tutorial:
1. Working with Stellar Java SDK
2. Creating a fully functional web application using Spring Boot, Spring Security and MySql
3. Open an Account and fund 10,000 XLM
4. Open another account and transfer 100 XLM from the first
5. View account details and blalances

## First things first  

download the SDK from https://github.com/stellar/java-stellar-sdk to your local file system

notice that this a jar file that needs to be imported into your local Maven repo.

## Import the jar into Maven repo

Use the following command to import the SDK jar file into your Maven repo:

mvn install:install-file -Dfile=/<path to the sdk jar> -DgroupId=<package name> -DartifactId=<packageId> -Dversion=<version> -Dpackaging=jar
  
For example : 

mvn install:install-file -Dfile=/Users/sunil_vishnubhotla/Downloads/stellar-sdk.jar -DgroupId=com.stellar.code -DartifactId=stellar -Dversion=0.1.14 -Dpackaging=jar

Then add the dependancy in your pom.xml file like so :

<dependency>
     <groupId>com.stellar.code</groupId>
     <artifactId>stellar</artifactId>
     <version>0.1.14</version>
</dependency>

Note: this dependancy is already added in the source.

To run the sample, install Maven, Java 1.8+, MySql and download the code. 
Edit the application.properties file to setup your DB connection.

And simply run this command in the source root

mvn springboot:run

