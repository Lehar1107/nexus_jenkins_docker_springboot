pipeline {
    agent any
    environment {
     imageName = "lowerupper"
     imageTag = "1.0.$env.BUILD_NUMBER"
  }

    stages {
        stage('Get Code') {
            steps {
                git branch: 'main', url: 'https://github.com/Lehar1107/nexus_jenkins_docker_springboot.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests=true'
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
                    
                    sh "docker build -t ${imageName}:${imageTag} ."
                    sh "docker image ls ${imageName}:${imageTag}"
                }
            }
        }
        
        
        stage('Push Docker Image to Nexus') {
            environment {
                NEXUS_CREDENTIALS = credentials('nexus-user-credentials')
            }
            steps {
                script {
                    def nexusUrl = "localhost:8082"
                    def nexusRepository = "docker-hosted"
                    def nexusrepourl = "http://192.168.1.76:8082/repository/docker-hosted/" 
                    def dockerImage = "${imageName}:${imageTag}"
                    def nexusImage = "${nexusUrl}/${nexusRepository}/${imageName}:${imageTag}"

                    withCredentials([usernamePassword(credentialsId: 'nexus-user-credentials', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                        withEnv(["DOCKER_LOGIN=\${NEXUS_USERNAME}", "DOCKER_PASSWORD=\${NEXUS_PASSWORD}"]) {
                            echo "DOCKER_USERNAME: ${env.NEXUS_USERNAME}"
                            echo "DOCKER_PASSWORD: ${env.NEXUS_PASSWORD}"
                            sh 'docker login -u ${env.NEXUS_USERNAME} -p ${env.NEXUS_PASSWORD} ${nexusUrl}'
                            sh "docker login -u $NEXUS_USERNAME -p $NEXUS_PASSWORD $nexusrepourl"
                            sh "docker tag ${dockerImage} ${nexusImage}"
                            sh "docker push ${nexusImage}"
                            sh "docker logout $nexusrepourl"
                        }
                    }
                }
            }
        }
		
		


        // stage('Deploy Application in kbernetes') {
        //     // environment {
        //     //     NEXUS_CREDENTIALS = credentials('nexus-credentials')
        //     // }
        //     steps {
        //         script {
        //             // sh "sed -i 's#<regex>#<replacement>#g' file_name"
        //             // kubectl apply -f ./deployment.yaml
        //             // if you apply deployment from kubenets client machine then
        //             sh "kubectl --kubeconfig=/home/main/Desktop/kubeconfig/config apply -f ./deployment.yaml"
        //         }
        //     }
        // }

        // stage('Push Docker Image to Nexus') {
        //     steps {
        //         script {
        //             def imageName = "kafka-monitor"
        //             def imageTag = "latest"
        //             def nexusUrl = "http://localhost:8081"
        //             def nexusRepository = "maven-snapshots"
        //             def nexusCredentialsId = "nexus-credentials"

        //             def dockerImage = "${imageName}:${imageTag}"
        //             def nexusImage = "${nexusUrl}/${nexusRepository}/${imageName}:${imageTag}"
                    
        //             withCredentials([usernamePassword(credentialsId: nexusCredentialsId, usernameVariable: 'admin', passwordVariable: 'Nexus@123')]) {
        //                 sh "docker tag ${dockerImage} ${nexusImage}"
        //                 sh "docker login -u ${NEXUS_USERNAME} -p ${NEXUS_PASSWORD} ${nexusUrl}"
        //                 sh "docker push ${nexusImage}"
        //             }
        //         }
        //     }
        // }
    }
    // post {
    //     always {
    //         // Clean up Docker images after the pipeline run
    //         script {
    //             sh "docker rmi ${NEXUS_URL}/${NEXUS_REPO}/my-java-app:${IMAGE_TAG}"
    //             sh "docker logout ${NEXUS_URL}"
    //         }
    //     }
    // }
}