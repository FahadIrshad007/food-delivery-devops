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
                    args '-u root' 
                }
            }
            steps {
                // Running your Selenium tests
                sh 'mvn test' 
            }
        }
    }

    post {
        always {
            script {
                // Grabbing the committer's email dynamically
                def committerEmail = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                
                if (committerEmail) {
                    emailext (
                        subject: "Assignment 3: Build ${currentBuild.result} - ${env.JOB_NAME}",
                        body: "The pipeline for commit ${env.GIT_COMMIT} finished with status: ${currentBuild.result}. View build here: ${env.BUILD_URL}",
                        to: "${committerEmail}",
                        recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                    )
                } else {
                    echo "Could not find committer email. Skipping email notification."
                }
            }
        }
    }
}