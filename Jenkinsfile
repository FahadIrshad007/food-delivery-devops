pipeline {
    agent any
    
    stages {
        stage('Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/FahadIrshad007/food-delivery-devops.git'
            }
        }

        stage('Test') {
            agent {
                docker {
                    image 'markhobson/maven-chrome:jdk-21'
                    args '-v /tmp:/tmp' 
                }
            }
            steps {
                // This runs your 15+ Selenium tests
                sh 'mvn test' 
            }
        }
    }

    post {
        always {
            // Grading Requirement: Email results back to the person who pushed code
            emailext (
                subject: "Assignment 3 Test Results: ${currentBuild.fullDisplayName}",
                body: "The test stage finished with status: ${currentBuild.result}. Please check Jenkins at http://16.16.149.7:8080 for details.",
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'CulpritsRecipientProvider']]
            )
        }
    }
}