pipeline {
    agent any
    environment {
        imageName = "lowerupper"
        imageTag = "1.0.${env.BUILD_NUMBER}"
    }

    stages {
        stage('Get Code') {
            steps {
                git branch: 'main', url: 'https://github.com/Lehar1107/nexus_jenkins_docker_springboot.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests=true'
            }
        }
     }
   }
