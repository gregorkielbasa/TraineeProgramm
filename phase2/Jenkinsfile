pipeline {
    agent any

    environment {
        APP_IMAGE = 'shop'
        CONTAINER_NAME = 'shop-container'
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-credentials')
    }

    stages {
        stage('Clone Repository') {
            steps {
                // Clone the repository containing the source code and Dockerfile
                git branch: 'feature/jenkins',
                    credentialsId: 'bec7a7b5-44ca-4c02-b36c-0893918bd09a',
                    url: 'https://github.com/gregorkielbasa/TraineeProgramm.git'
            }
        }

        stage('Build JAR with Maven') {
            agent {
                docker {
                    image 'maven:3.9.8-eclipse-temurin'
                    args '-v maven_local_repository:/root/.m2'
                }
           }
            steps {
                    // Use Maven Docker image to build the JAR file without running tests
                    sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Run Tests') {
            agent {
                docker {
                    image 'maven:3.9.8-eclipse-temurin'
                    args '-v maven_local_repository:/root/.m2'
                }
           }
            steps {
                    // Use Maven Docker image to run the tests
                        sh 'mvn test'
            }
        }

        stage('Stop and remove running containers') {
            steps {
                script {
                  try {
                      sh 'docker stop ${CONTAINER_NAME}'
                      sh 'docker system prune -f'
                  } catch (Exception e) {
                      echo 'Exception occurred: ' + e.toString()
                      echo 'continues to the next stage'
                  }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image from the Dockerfile
                    sh 'docker build -t gregorkielbasa/${APP_IMAGE}:$BUILD_NUMBER .'
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Run the Docker container
                    sh 'docker run -d -p 8080:8080 --name ${CONTAINER_NAME} gregorkielbasa/${APP_IMAGE}:$BUILD_NUMBER'
                }
            }
        }

        stage('Login') {
            steps {
                sh 'docker login -u $DOCKERHUB_CREDENTIALS_USR -p $DOCKERHUB_CREDENTIALS_PSW'
            }
        }

        stage('Push Docker Image') {
            steps {
                sh 'docker push gregorkielbasa/${APP_IMAGE}:$BUILD_NUMBER'
            }
        }
    }

    post {
        always {
            cleanWs()
            sh 'docker logout'
        }
    }
}
