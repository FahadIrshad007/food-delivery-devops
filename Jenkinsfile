pipeline {
    agent any 

    stages { // <--- Added this required wrapper
        stage('Clone') {
            steps {
                // WipeWorkspace ensures we don't get "Permission Denied" from previous root runs
                checkout([$class: 'GitSCM', 
                    branches: [[name: 'main']], 
                    extensions: [[$class: 'WipeWorkspace']], 
                    userRemoteConfigs: [[url: 'https://github.com/FahadIrshad007/food-delivery-devops.git']]
                ])
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
                // Moving into the subfolder where pom.xml exists before running tests
                sh 'cd selenium-test-project && mvn clean test -U'
            }
        }
    } // <--- End of stages block

    post {
        always {
            script {
                // Capture the email of the person who made the commit (Fahad or Qasim)
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