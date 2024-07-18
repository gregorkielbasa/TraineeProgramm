pipeline {
    agent any

    environment {
        APP_IMAGE = 'trainee-app'
        CONTAINER_NAME = 'trainee-app-container'
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-credentials')
    }

    stages {
        stage('Clone Git Repository') {
            steps {
                // Clone the repository containing the source code and Dockerfile
                git branch: 'feature/jenkins',
                    credentialsId: 'git-hub-credentials',
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
                    stash includes: 'target/*.jar', name: 'targetfiles'
            }
        }

        stage('Run Maven Tests') {
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
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Stop and remove running containers') {
            steps {
                script {
                    try {
                        sh 'docker-compose -f docker.yaml down'
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
                    unstash 'targetfiles'
                    sh 'docker build -t gregorkielbasa/${APP_IMAGE}:$BUILD_NUMBER .'
                }
            }
        }

        stage('Set up the latest version') {
            steps {
                script {
                    // Build the Docker image from the Dockerfile
                    sh 'docker image tag gregorkielbasa/${APP_IMAGE}:$BUILD_NUMBER gregorkielbasa/${APP_IMAGE}:latest'
                }
            }
        }

        stage('Run Docker Compose') {
            steps {
                script {
                    // Run the Docker container
                    sh 'docker-compose -f docker.yaml up -d'
                }
            }
        }

        stage('Log in in DockerHub') {
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
