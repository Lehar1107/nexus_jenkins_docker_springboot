pipeline {
    agent any
    environment {
        imageName = "newbuild"
        imageTag = "1.0.${env.BUILD_NUMBER}"
        nexusUrl = "192.168.1.17:8082"
        nexusRepository = "docker-hosted"
        nexusrepourl = "http://192.168.1.17:8082/repository/docker-hosted/"
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
        stage('Push docker image to Nexus') {
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
	    stage('Prepare YAML') {
            steps {
                script {
                    def yamlContent = readFile('nexus/new.yaml')
                    def modifiedContent = yamlContent.replaceAll('\\$\\{BUILD_NUMBER\\}', env.BUILD_NUMBER)
                    writeFile file: 'nexus/new.yaml', text: modifiedContent
                }
            }
        }
	    
        stage('List Files') {
            steps {
                dir('nexus') {
                    bat 'dir'
                }
            }
        }

        stage('Deploy Application in Kubernetes') {
		    environment {
                NEXUS_CREDENTIALS = credentials('nexus-user-credentials')
            }
            steps {
                script {
					
                    bat "kubectl --kubeconfig=C:/Users/LEHAR/.kube/config apply -f nexus/new.yaml"
                }
            }
        }	
	    stage('Push pod Image to Nexus') {
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
    }
}
