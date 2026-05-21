pipeline {

    agent any

    tools {
        maven 'Maven-3'
    }

    environment {
        TOMCAT_WEBAPPS = '/opt/tomcat/webapps'
    }

    stages {

        stage('Build Application') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Deploy WAR') {
            steps {

                sh '''
                cp target/*.war $TOMCAT_WEBAPPS/employeeapp.war
                '''

            }
        }

        stage('Restart Tomcat') {
            steps {

                sh '''
                sudo systemctl restart tomcat
                '''

            }
        }
    }
}
