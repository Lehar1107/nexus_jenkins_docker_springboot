pipeline {
    agent any
    environment {
        imageName = "newbuild"
        imageTag = "1.0.${env.BUILD_NUMBER}"
        nexusUrl = "192.168.1.41:8082"
        nexusRepository = "docker-hosted"
        nexusrepourl = "http://192.168.1.41:8082/repository/docker-hosted/"
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
					//def nexusUsername = NEXUS_CREDENTIALS?.username
					//def nexusPassword = NEXUS_CREDENTIALS?.password
					
					//echo "Nexus Username: ${nexusUsername}"
					//echo "Nexus Password: ${nexusPassword}"
					
					withCredentials([usernamePassword(credentialsId: 'nexus-user-credentials', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                        withEnv(["DOCKER_LOGIN=\${NEXUS_USERNAME}", "DOCKER_PASSWORD=\${NEXUS_PASSWORD}"]) {
                            // echo "DOCKER_USERNAME: ${env.NEXUS_USERNAME}"
                            // echo "DOCKER_PASSWORD: ${env.NEXUS_PASSWORD}"
                            // sh 'docker login -u ${env.NEXUS_USERNAME} -p ${env.NEXUS_PASSWORD} ${nexusUrl}'
                            bat "docker login -u $NEXUS_USERNAME -p $NEXUS_PASSWORD $nexusrepourl"
                            bat "docker tag ${dockerImage} ${nexusImage}"
                            bat "docker push ${nexusImage}"
                            bat "docker logout $nexusrepourl"
                        }
                    }
                }
            }
        }
		
		stage('Deploy Application in kubernetes') {
            steps {
                script {
                     // sh "sed -i 's#<regex>#<replacement>#g' file_name"
                    // kubectl apply -f ./deployment.yaml
                     // if you apply deployment from kubenets client machine then
					 //"C:\Users\LEHAR\.kube\config"
					 // Replace the BUILD_NUMBER placeholder in the YAML file with the actual build number
                    bat "sed -i 's/\\\$\\{BUILD_NUMBER\\}/${env.BUILD_NUMBER}/g' new.yaml"
                    bat "kubectl --kubeconfig=C:/Users/LEHAR/.kube/config apply -f ./new.yaml"
                 }
             }
        }

    }
}
