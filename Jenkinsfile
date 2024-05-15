pipeline {
    agent any
    environment {
        imageName = "lowerupper"
        imageTag = "1.0.${env.BUILD_NUMBER}"
        nexusUrl = "localhost:8082"
        nexusRepository = "docker-hosted"
        nexusrepourl = "http://192.168.1.76:8082/repository/docker-hosted/"
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
        stage('Build Docker Image') {
            steps {
                script {
                    def dockerfile = """
                        FROM openjdk:17-jdk
                        COPY target/springboot-docker-nexus-0.0.1-SNAPSHOT.jar /app/my-spring-boot-app.jar
                        WORKDIR /app
                        ENTRYPOINT ["java", "-jar", "my-spring-boot-app.jar"]
                    """
                    writeFile file: 'Dockerfile', text: dockerfile
                    bat "docker build -t ${imageName}:${imageTag} ."
                    bat "docker image ls ${imageName}:${imageTag}"
                }
            }
        }
        stage('Push Docker Image to Nexus') {
            environment {
                NEXUS_CREDENTIALS = credentials('nexus-user-credentials')
            }
            steps {
                script {
                    def dockerImage = "${imageName}:${imageTag}"
                    def nexusImage = "${nexusUrl}/${nexusRepository}/${imageName}:${imageTag}"
					def nexusCredentials = credentials('nexus-user-credentials')
					def nexusUsername = nexusCredentials?.username
					def nexusPassword = nexusCredentials?.password
                    withCredentials([usernamePassword(credentialsId: 'nexus-user-credentials', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                        withEnv(["DOCKER_LOGIN=${nexusUsername}", "DOCKER_PASSWORD=${nexusPassword}"]) {
                            echo "DOCKER_USERNAME: ${nexusUsername}"
                            echo "DOCKER_PASSWORD: ${nexusPassword}"
                            bat 'echo ${nexusPassword} | docker login -u ${nexusUsername} --password-stdin ${nexusUrl}'
                            bat "docker tag ${dockerImage} ${nexusImage}"
                            bat "docker push ${nexusImage}"
                            bat "docker logout ${nexusUrl}"
                        }
                    }
                }
            }
        }
    }
}
