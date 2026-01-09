pipeline {
    agent any

    environment {
        PATH = "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:$PATH"
    }

    tools {
        maven 'maven'
    }

    stages {
        stage('Fetch') {
            steps {
                git branch: 'main', url: 'https://github.com/Vedavyaas/WebScrapper.git'
            }
        }
        stage('Clean and test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('JAR package') {
            steps{
                sh 'mvn package'
            }
        }
        stage('Image build') {
            steps{
                sh 'docker build -t web-scrapper-app:v1 .'
            }
        }
        stage('Deploy image') {
            steps{
                sh 'docker rm -f web-scrapper-live || true'
                sh 'docker run -d --name web-scrapper-live -p 8081:9000 web-scrapper-app:v1'
            }
        }
    }
    post {
        always {
            sh 'docker image prune -f'
        }
    }
}