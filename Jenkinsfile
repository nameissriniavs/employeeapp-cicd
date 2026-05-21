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
