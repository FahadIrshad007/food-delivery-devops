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
            emailext (
                subject: "Build ${currentBuild.result}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Build: ${env.BUILD_URL}
                         Status: ${currentBuild.result}
                         Commit: ${env.GIT_COMMIT}""",
                recipientProviders: [
                    [$class: 'DevelopersRecipientProvider'],
                    [$class: 'CulpritsRecipientProvider']
                ]
            )
        }
    }
}