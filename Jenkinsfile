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
	stage('Build Docker Image') {
            steps {
                script {
                    def imageName = "lowerupper"
                    def imageTag = "latest"
                    
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
                    $env:nexusUrl = "localhost:8082"
                    $env:nexusRepository = "docker-hosted"
                    $env:nexusrepourl = "http://192.168.1.76:8082/repository/docker-hosted/" 
                    $env:dockerImage = "${imageName}:${imageTag}"
                    $env:nexusImage = "${nexusUrl}/${nexusRepository}/${imageName}:${imageTag}"

                    withCredentials([usernamePassword(credentialsId: 'nexus-user-credentials', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                        withEnv(["DOCKER_LOGIN=\${NEXUS_USERNAME}", "DOCKER_PASSWORD=\${NEXUS_PASSWORD}"]) {
                            echo "DOCKER_USERNAME: ${env.NEXUS_USERNAME}"
                            echo "DOCKER_PASSWORD: ${env.NEXUS_PASSWORD}"
                            bat 'docker login -u ${env.NEXUS_USERNAME} -p ${env.NEXUS_PASSWORD} ${nexusUrl}'
                            bat "docker login -u $NEXUS_USERNAME -p $NEXUS_PASSWORD $nexusrepourl"
                            bat "docker tag ${dockerImage} ${nexusImage}"
                            bat "docker push ${nexusImage}"
                            bat "docker logout $nexusrepourl"
                        }
                    }
				}
			}
		}
     }
   }
