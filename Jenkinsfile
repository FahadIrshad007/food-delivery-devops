pipeline {
    agent any 
    
    stages {
        stage('Clone') {
            steps {
                // Ensure we are pulling the latest from the main branch
                git branch: 'main', url: 'https://github.com/FahadIrshad007/food-delivery-devops.git'
            }
        }

        stage('Test') {
            agent {
                docker {
                    image 'markhobson/maven-chrome:jdk-21'
                    // -u root is essential to avoid the "Could not create local repository" error
                    args '-u root' 
                }
            }
            steps {
                // Moving into the subfolder where pom.xml exists before running tests
                sh 'cd selenium-test-project && mvn clean test -U'
            }
        }
    }

    post {
        always {
            script {
                // This captures the email of the person who made the commit (Fahad or Qasim)
                def committerEmail = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                
                if (committerEmail) {
                    emailext (
                        subject: "Assignment 3: Build ${currentBuild.result} - ${env.JOB_NAME}",
                        body: """<p>The pipeline for commit ${env.GIT_COMMIT} finished with status: <b>${currentBuild.result}</b>.</p>
                                 <p>Check the full console output here: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                        mimeType: 'text/html',
                        to: "${committerEmail}",
                        recipientProviders: [[$class: 'DevelopersRecipientProvider']]
                    )
                } else {
                    echo "Dynamic email detection failed. No notification sent."
                }
            }
        }
    }
}