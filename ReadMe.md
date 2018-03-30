# AWS Lambda Java REST API Serverless Demo Project 



This project is to demo using AWS Lambada, RDS, API Gateway and Serverless framework to construct some basic REST API. It uses AWS RDS as the database.

## Prerequisites
Before you start, make sure you have the following:
* Active AWS account - AWS Lambda, API Gateway, RDS are used in this project.
* The AWS Command Line Interface (CLI) - It is used to create the MySQL RDS database. It will also be used to configure user profile to run serverless framework.
* An AWS user profile with sufficient permissions - So that can allow you to execute RDS creation, install Lambda functions and install API on API Gateway.
* Java 8 
* Serverless Framework 1.26.1 - This is the version that is used here. Older version may work as well. (https://serverless.com)

## Installation

### Install AWS RDS DB Instance (MySQL)

The AWSCLI command below is to create a MySQL db instance on RDS. You can revise it to create other DB engine
Note: Choose your own db-instance-identifier, engine, db-name, master master-user-password, AWS user profile. To avoid permission issue, I recommend to use a user profile with full access to RDS, Lambda, API Gateway.

```sh
$ aws rds create-db-instance \
    --db-instance-identifier MySQLDBInstanceName \
    --db-instance-class db.r3.large \
    --engine MySQL \
    --port 3006 \
    --allocated-storage 5 \
    --db-name customer \
    --master-username master \
    --master-user-password master123 \
    --backup-retention-period 3 \
    --profile admin
```
You can use any SQL client to verify the database is successfully created. The jdbc connection url will be similar to this: jdbc:mysql://MySQLDBInstanceName.XXXXXXXX.us-east-1.rds.amazonaws.com/customer

### Create customer Table in DB
Execute the SQL in any SQL client to create the table which will be used for REST API.
```sh
create table customer (
    id int NOT NULL AUTO_INCREMENT,
    name varchar(255),  
    PRIMARY KEY (id)
);
```

## Build 
### Edit hibernate.cfg.xml
Before you start, make sure you edit hibernate.cfg.xml and replace the following properties with your own parameter value.
```sh
        <property name="hibernate.connection.url">jdbc:mysql://MySQLDBInstanceName.XXXXXXXXX.us-east-1.rds.amazonaws.com/customer</property>
		<property name="hibernate.connection.username">master</property>
		<property name="hibernate.connection.password">master123</property>
```
### Building for source
Run in your project directory
```sh
$ mvn clean install
```
## Deploy to AWS 
When the build is successful, you can use serverless to deploy to AWS.
```sh
$ serverless deploy
```
If the deployment is successful, you will see the endpoints shown as below:
``` sh
Serverless: Stack update finished...
Service Information
service: customer
stage: dev
region: us-east-1
stack: customer-dev
api keys:
  None
endpoints:
  GET - https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/hello
  GET - https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/customers/{name}
  POST - https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/customers
  DELETE - https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/customers/{name}
  GET - https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/fibonacci
functions:
  hello: customer-dev-hello
  getCustomers: customer-dev-getCustomers
  addCustomer: customer-dev-addCustomer
  deleteCustomer: customer-dev-deleteCustomer
  fibonacci: customer-dev-fibonacci
Serverless: Removing old service versions...
```
## TEST
You can test in the terminal or use Postman to test the endpoints. Some endpoints will need query string parameters or path parameters.

Below are the command example to test the five endpoints above. Please use your actual endpoints for testing.

1.  Hello World
```sh
$ curl https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/hello
```
2. Get the customer with name "John" 
```sh
$ curl https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/customers/John
```
3. Add a new customer
```sh
curl -d '{"name":"Mary"}' -H "Content-Type:application/json"  -X POST https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/customers
```
4. Delete a customer with name "John"
```sh
$ curl -X DELETE https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/customers/John
```
5. Display the first 5 numbers in Fibonacci Array
```sh
$ curl https://xxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/fibonacci?n=5
```
## Todos
 - Write MORE Tests
 - Add Night Mode

License
----

MIT


**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [markdown-it]: <https://github.com/markdown-it/markdown-it>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
   [PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
   [PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>
