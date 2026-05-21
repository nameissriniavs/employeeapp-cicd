# Complete CI/CD Pipeline Project Using Jenkins, Spring Boot, Tomcat, Maven, GitHub, and AWS EC2

## Project Overview

This project demonstrates a complete CI/CD pipeline deployment using:

* GitHub
* Jenkins
* Maven
* Spring Boot
* Apache Tomcat 10
* AWS EC2 Ubuntu Server
* Java 21

The pipeline automatically:

1. Pulls source code from GitHub
2. Builds the application using Maven
3. Creates a WAR file
4. Deploys the WAR file to Apache Tomcat
5. Restarts Tomcat automatically
6. Publishes the updated application

---

# Architecture

GitHub → Jenkins → Maven Build → WAR Deployment → Tomcat → AWS EC2

---

# Prerequisites

## AWS EC2 Instance

Recommended:

* Ubuntu 24.04 LTS
* t3.micro or t3.small
* Security Group Ports:

  * 22 (SSH)
  * 8080 (Jenkins)
  * 8081 (Tomcat)

---

# Step 1 — Connect to EC2

```bash
ssh -i your-key.pem ubuntu@YOUR_PUBLIC_IP
```

---

# Step 2 — Update Ubuntu Server

```bash
sudo apt update -y
sudo apt upgrade -y
```

---

# Step 3 — Install Java 21

```bash
sudo apt install -y openjdk-21-jdk
```

Verify:

```bash
java --version
```

Expected:

```bash
openjdk 21
```

---

# Step 4 — Install Maven

```bash
sudo apt install -y maven
```

Verify:

```bash
mvn -version
```

---

# Step 5 — Install Git

```bash
sudo apt install -y git
```

Verify:

```bash
git --version
```

---

# Step 6 — Install Jenkins

## Install Dependencies

```bash
sudo apt install -y fontconfig daemon net-tools
```

## Download Jenkins Package

```bash
cd /tmp

wget https://get.jenkins.io/debian-stable/jenkins_2.516.1_all.deb
```

## Install Jenkins

```bash
sudo dpkg -i /tmp/jenkins_2.516.1_all.deb
```

Fix dependency issues if required:

```bash
sudo apt --fix-broken install -y
```

---

# Step 7 — Start Jenkins

```bash
sudo systemctl daemon-reload

sudo systemctl enable jenkins

sudo systemctl start jenkins
```

Check status:

```bash
sudo systemctl status jenkins
```

Expected:

```bash
active (running)
```

---

# Step 8 — Access Jenkins

Open browser:

```text
http://YOUR_PUBLIC_IP:8080
```

Get initial password:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Complete:

* Install Suggested Plugins
* Create Admin User

---

# Step 9 — Install Apache Tomcat 10

## Create Tomcat User

```bash
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat
```

## Download Tomcat

```bash
cd /tmp

wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.47/bin/apache-tomcat-10.1.47.tar.gz
```

## Create Installation Directory

```bash
sudo mkdir -p /opt/tomcat
```

## Extract Tomcat

```bash
sudo tar -xzf apache-tomcat-10.1.47.tar.gz \
-C /opt/tomcat --strip-components=1
```

## Set Permissions

```bash
sudo chown -R tomcat:tomcat /opt/tomcat

sudo chmod -R u+x /opt/tomcat/bin
```

---

# Step 10 — Change Tomcat Port

Edit:

```bash
sudo nano /opt/tomcat/conf/server.xml
```

Find:

```xml
<Connector port="8080"
```

Replace with:

```xml
<Connector port="8081"
```

Save file.

---

# Step 11 — Configure Tomcat Service

Create service file:

```bash
sudo nano /etc/systemd/system/tomcat.service
```

Paste:

```ini
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_BASE=/opt/tomcat"

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

Restart=always

[Install]
WantedBy=multi-user.target
```

Save file.

---

# Step 12 — Start Tomcat

```bash
sudo systemctl daemon-reload

sudo systemctl enable tomcat

sudo systemctl start tomcat
```

Check:

```bash
sudo systemctl status tomcat
```

Expected:

```bash
active (running)
```

Open browser:

```text
http://YOUR_PUBLIC_IP:8081
```

---

# Step 13 — Create Spring Boot Application

## Create Project Structure

```bash
mkdir employeeapp
cd employeeapp
```

---

# Step 14 — Create pom.xml

Create file:

```bash
nano pom.xml
```

Paste:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.srinivas</groupId>
    <artifactId>employeeapp</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <packaging>war</packaging>

    <name>employeeapp</name>
    <description>Employee Management Application</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

---

# Step 15 — Create Application Class

Create directories:

```bash
mkdir -p src/main/java/com/srinivas/employeeapp
```

Create file:

```bash
nano src/main/java/com/srinivas/employeeapp/EmployeeappApplication.java
```

Paste:

```java
package com.srinivas.employeeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EmployeeappApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EmployeeappApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeappApplication.class, args);
    }
}
```

---

# Step 16 — Create Controller

Create directory:

```bash
mkdir -p src/main/java/com/srinivas/employeeapp/controller
```

Create file:

```bash
nano src/main/java/com/srinivas/employeeapp/controller/HomeController.java
```

Paste:

```java
package com.srinivas.employeeapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("message",
                "CI/CD Pipeline Project - Spring Boot Application Successfully Deployed using Jenkins Pipeline on AWS EC2");

        return "index";
    }
}
```

---

# Step 17 — Create HTML Page

Create directory:

```bash
mkdir -p src/main/resources/templates
```

Create file:

```bash
nano src/main/resources/templates/index.html
```

Paste:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Employee App</title>
</head>
<body>

<h1 th:text="${message}"></h1>

</body>
</html>
```

---

# Step 18 — Build Application

```bash
mvn clean package
```

Expected WAR:

```bash
target/employeeapp-0.0.1-SNAPSHOT.war
```

---

# Step 19 — Deploy WAR to Tomcat

```bash
sudo cp target/employeeapp-0.0.1-SNAPSHOT.war \
/opt/tomcat/webapps/employeeapp.war
```

Set permissions:

```bash
sudo chown tomcat:tomcat /opt/tomcat/webapps/employeeapp.war
```

Restart Tomcat:

```bash
sudo systemctl restart tomcat
```

Open browser:

```text
http://YOUR_PUBLIC_IP:8081/employeeapp
```

---

# Step 20 — Push Project to GitHub

## Initialize Git

```bash
git init
```

## Add Files

```bash
git add .
```

## Commit

```bash
git commit -m "Initial commit"
```

## Add Remote Repository

```bash
git remote add origin https://github.com/YOUR_USERNAME/employeeapp-cicd.git
```

## Push Code

```bash
git branch -M main

git push -u origin main
```

---

# Step 21 — Configure Jenkins Plugins

Go to:

Manage Jenkins → Plugins

Install:

* Git
* Pipeline
* Pipeline Stage View
* Maven Integration

Restart Jenkins.

---

# Step 22 — Configure Maven in Jenkins

Go to:

Manage Jenkins → Tools

Add Maven:

```text
Name: Maven-3
```

Check:

```text
Install Automatically
```

Save.

---

# Step 23 — Give Jenkins Access to Tomcat

```bash
sudo usermod -aG tomcat jenkins
```

```bash
sudo chmod -R 775 /opt/tomcat/webapps
```

```bash
sudo chown -R tomcat:tomcat /opt/tomcat/webapps
```

Restart Jenkins:

```bash
sudo systemctl restart jenkins
```

---

# Step 24 — Configure Passwordless Sudo

Edit sudoers:

```bash
sudo visudo
```

Add this line at the bottom:

```text
jenkins ALL=(ALL) NOPASSWD: /bin/cp, /bin/systemctl
```

Save file.

---

# Step 25 — Create Jenkinsfile

Create file:

```bash
nano Jenkinsfile
```

Paste:

```groovy
pipeline {

    agent any

    tools {
        maven 'Maven-3'
    }

    environment {
        DEPLOY_DIR = '/opt/tomcat/webapps'
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy') {
            steps {

                sh '''
                sudo cp target/*.war $DEPLOY_DIR/employeeapp.war
                sudo systemctl restart tomcat
                '''

            }
        }
    }
}
```

---

# Step 26 — Push Jenkinsfile

```bash
git add Jenkinsfile

git commit -m "Added Jenkins pipeline"

git push origin main
```

---

# Step 27 — Create Jenkins Pipeline Job

Open Jenkins:

```text
http://YOUR_PUBLIC_IP:8080
```

Create:

```text
New Item
```

Name:

```text
employeeapp-pipeline
```

Choose:

```text
Pipeline
```

Under Pipeline:

Definition:

```text
Pipeline script from SCM
```

SCM:

```text
Git
```

Repository URL:

```text
https://github.com/YOUR_USERNAME/employeeapp-cicd.git
```

Branch:

```text
*/main
```

Script Path:

```text
Jenkinsfile
```

Save.

---

# Step 28 — Run Pipeline

Click:

```text
Build Now
```

Pipeline stages:

* Build
* Deploy

Expected result:

```text
Finished: SUCCESS
```

---

# Step 29 — Verify Application Deployment

Open browser:

```text
http://YOUR_PUBLIC_IP:8081/employeeapp
```

Expected output:

```text
CI/CD Pipeline Project - Spring Boot Application Successfully Deployed using Jenkins Pipeline on AWS EC2
```

---

# Step 30 — Test CI/CD Automation

Modify controller message:

```java
model.addAttribute("message",
        "Automated CI/CD Deployment Successful");
```

Commit changes:

```bash
git add .

git commit -m "Updated deployment message"

git push origin main
```

Run Jenkins build.

Refresh browser.

Updated application will be deployed automatically.

---

# Troubleshooting

## Jenkins Permission Denied

Fix:

```bash
sudo visudo
```

Add:

```text
jenkins ALL=(ALL) NOPASSWD: /bin/cp, /bin/systemctl
```

---

## EC2 Instance Hanging During Build

Create swap memory:

```bash
sudo fallocate -l 2G /swapfile
```

```bash
sudo chmod 600 /swapfile
```

```bash
sudo mkswap /swapfile
```

```bash
sudo swapon /swapfile
```

```bash
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

Verify:

```bash
free -h
```

---

# Final Project Summary

This project successfully implements:

* Continuous Integration
* Continuous Deployment
* Automated WAR deployment
* Jenkins Pipeline automation
* AWS EC2 Linux administration
* Spring Boot application deployment
* Tomcat application hosting
* GitHub source control integration

---

# Skills Demonstrated

* Linux Administration
* Jenkins CI/CD
* Git & GitHub
* Maven Build Automation
* Spring Boot Deployment
* Apache Tomcat Administration
* AWS EC2 Management
* Java 21 Configuration
* Troubleshooting & Monitoring

---

# Future Improvements

* GitHub Webhooks
* Docker Containerization
* Kubernetes Deployment
* Terraform Infrastructure Automation
* Ansible Configuration Management
* Nginx Reverse Proxy
* HTTPS SSL Configuration
* Monitoring with Prometheus & Grafana

I’ve created the complete end-to-end project documentation with:

* Full project description
* Complete working Spring Boot code
* Jenkinsfile
* Maven configuration
* Tomcat setup
* Jenkins installation
* AWS EC2 setup
* GitHub integration
* CI/CD pipeline configuration
* Deployment steps
* Troubleshooting section
* Memory/swap fixes
* Production improvement suggestions

The guide is structured step-by-step so you can use it for:

* learning
* GitHub README
* interview preparation
* resume projects
* portfolio documentation
* future practice labs.
