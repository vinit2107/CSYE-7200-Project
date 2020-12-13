# Scion - Data Transformation for Stock Analysis

![Scala CI](https://github.com/vinit2107/CSYE-7200-Project-Grp-5/workflows/Scala%20CI/badge.svg)

<img src="https://images.unsplash.com/photo-1569025690938-a00729c9e1f9?ixid=MXwxMjA3fDB8MHxzZWFyY2h8M3x8ZmluYW5jZXxlbnwwfHwwfA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1934&q=80" width="1100" height="500">

Image Source - [Unsplash](https://images.unsplash.com/photo-1569025690938-a00729c9e1f9?ixid=MXwxMjA3fDB8MHxzZWFyY2h8M3x8ZmluYW5jZXxlbnwwfHwwfA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=600&q=60)

### Overview

The project aims to give user the previledge to maintain a portfolio of stocks and perform statistical analysis on the data. There are two main parts of the project. The first part is the User Interface which allows the user to create a account and add a list of stocks that the user wants to add to his/her portfolio. The User Interface also allows the user to initiate spark jobs form the UI to perform the necessary transformations. This forms the second part of the project, performing data transformation. The user has the liberty to determine the tools that he wants to use on the data transformation layer. The required changes to be made in the configuration file will be explained below.
The two modules, UI and data transformation module are in these folder resp.:
1. Scion-Project
2. Spark-Job

### Steps to Reproduce

**I. UI**

In order to reproduce the UI, clone the repository and configure the [application.conf](https://github.com/vinit2107/CSYE-7200-Project-Grp-5/blob/main/Scion-Project/conf/application.conf) configuration file. Provide the jdbc connection URL for a MySQL database. This MyQSL database should have the following tables, 

1. **USERS** - This table stores the information user fills up while signing up. 

*CREATE TABLE USERS (USERNAME VARCHAR(24), PASSWORD VARCHAR(24), NAME TEXT, EMAIL VARCHAR(50), CITY TEXT);*

2. **USERSTOCKS** - This table stores the username and stocks the user has selected to be added to the portfolio.

*CREATE TABLE USERSTOCKS (USERNAME VARCHAR(24), STOCKS VARCHAR(24));*

3. **STOCKS** - This table stores the list of all the stocks.

*CREATE TABLE STOCKS (STOCKNAME VARCHAR(24), SHORTNAME(24));*

The user also needs to configure AWS related credentials. The application requires the user to configure the credentials using the [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html). The application also requires the additional jars, Data-Transformer.jar, [configuration.properties](https://github.com/vinit2107/CSYE-7200-Project-Grp-5/blob/main/Spark-Job/src/main/resources/configuration.properties) to be uploaded onto S3 and provide its location in the application.conf. The properties and the reason for defining the requirements are defined below:

1. **emr.name** - The string value assigned against this property will be used as the name of the EMR instance.
2. **emr.logURI** - This is used to defined the location of logs from the EMR cluster. This has to be a location on S3.
3. **emr.ec2SubnetId** - The value should be the subnet you want the cluster to be using. 
4. **emr.ec2KeyName** - The application requires the host to have KeyPairs already assigned. You can configure this using the IAM in AWS. This is used when you need to login to the cluster to check yarn logs.
5. **emr.instanceCount** -This value determines the number to instances that the cluster will have. This value should be a positive value greater than 0. 
6. **emr.jarsPath** - This is used to define any additional jars the data transformer will need to be passed to the driver while executing the spark job. The application has been tested using mysql-connector-java-8.0.20.jar and redshift-jdbc42-2.0.0.0.jar jars. 
7. **emr.filesPath** - This should be the path for configuration.properties on S3.
8. **emr.mainJarPath** - This should be path for Data-Transformer.jar on S3.


**II. Spark Job - Data Transformation Module**

This module gives user the freedom to choose the destination and input data from any one of S3, Redshift and MySQL servers. For functioning of the module, generation of two tables in MySQL is required. 

1. **CONTROL_INFO** - This table is used to record the ingestioned data. This table will also have the path to the file that has been ingested. If there are multiple files, the user can insert multiple records or insert a single record pointing to the directory the data is ingested into. 

*CREATE TABLE CONTROL_INFO (ID INT PRIMARY KEY AUTO_INCREMENT, TICKER VARCHAR(10), PATH TEXT, INGESTION_TIME TIMESTAMP DEFAULT NOW())*

2. **JOB_HISTORY** - This tableis used to record the status of the job completed, either SUCCESS or FAILURE. While checking for the new entries in CONTROL_INFO, the program will check the time for the latest SUCCESS entry for that entity in JOB_HISTORY table. Only those entries in CONTROL_INFO which have been inserted after the latest SUCCESS job for that entity will be filtered out for processing. 

*CREATE TABLE JOB_HISTORY (ID INT PRIMARY KEY AUTO_INCREMENT, APPLICATION_ID VARCHAR(50), TICKER varchar(20), STATUS VARCHAR(10), START_TIME TIMESTAMP, END_TIME TIMESTAMP DEFAULT NOW())*

[configuration.properties](https://github.com/vinit2107/CSYE-7200-Project-Grp-5/blob/main/Spark-Job/src/main/resources/configuration.properties) also need to be configured. The properties and the reason for defining the properties is given below:

1. **jdbc.mysql.url** - The URL to the MySQL database.
2. **jdbc.mysql.username** - The username required for logging into the MySQL database
3. **jdbc.mysql.password** - The password required for logging into the MySQL database
4. **jdbc.redshift.url** - The URL used to connect to the Redshift cluster. (Writing data to Redshift was very slow. Suggested to write data onto S3 and then create a external table on S3 in Redshift. The required steps are defined [here](https://docs.aws.amazon.com/redshift/latest/dg/c-getting-started-using-spectrum-create-external-table.html))
5. **jdbc.redshift.username** - Username which has access to the database in redshift.
6. **jdbc.redshift.password** - Password of the user used to access the redshift database
7. **ingestion.destination.type** - Should define the server the ingested data is hosted on. By default, has been assigned with S3
8. **output.destination.type** - Should define the server where the output of the spark job has to be written. By default has been assigned with S3
9. **output.destination.tablename** - Should define the table to which the output has to be written. In case of S3, this should point to a directory.
10. **aws.s3.accesskey** - Access key generated for a user in AWS. This is needed to read the files from S3. Make sure the user has access to read and write into S3.
11. **aws.s3.secretkey** - The secret access key for the user in AWS> This is needed to read the files from S3.







