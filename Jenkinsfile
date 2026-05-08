pipeline {
    agent any

    environment {
        APP_URL = 'http://13.63.130.174:8300'
        COMPOSE = 'docker-compose -f docker-compose.yml -p foodapp'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Deploy Application') {
            steps {
                sh '''
                    $COMPOSE down 2>/dev/null || true
                    $COMPOSE up -d
                    sleep 15
                '''
            }
        }

        stage('Run Selenium Tests') {
            steps {
                sh '''
                    mkdir -p selenium-tests
                    docker run --rm --network host \
                        -e BASE_URL=$APP_URL \
                        -v ${WORKSPACE}/tests:/tests \
                        -v ${WORKSPACE}/selenium-tests:/results \
                        -w /tests \
                        bilal888/selenium-test:latest \
                        pytest -v \
                        --html=/results/report.html \
                        --self-contained-html \
                        --junitxml=/results/results.xml \
                        --tb=short
                '''
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/selenium-tests/results.xml'
            }
        }
    }

    post {
        always {
            script {
                sh "git config --global --add safe.directory ${env.WORKSPACE}"

                def committer = sh(
                    script: "git log -1 --pretty=format:'%ae' || echo ''",
                    returnStdout: true
                ).trim() ?: 'businessfahad123@gmail.com'

                archiveArtifacts artifacts: 'selenium-tests/report.html', allowEmptyArchive: true

                int total = 0, failures = 0, skipped = 0, passed = 0

                def xmlLine = sh(
                    script: 'grep -h "<testsuite" selenium-tests/results.xml 2>/dev/null || true',
                    returnStdout: true
                ).trim()

                if (xmlLine) {
                    def t = (xmlLine =~ /tests="(\\d+)"/)
                    def f = (xmlLine =~ /failures="(\\d+)"/)
                    def s = (xmlLine =~ /skipped="(\\d+)"/)

                    if (t) total = t[0][1] as int
                    if (f) failures = f[0][1] as int
                    if (s) skipped = s[0][1] as int

                    passed = Math.max(total - failures - skipped, 0)
                }

                emailext(
                    to: committer,
                    subject: "Build ${currentBuild.result}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
Test Summary (Build #${env.BUILD_NUMBER})

Total Tests:   ${total}
Passed:        ${passed}
Failed:        ${failures}
Skipped:       ${skipped}

Build URL: ${env.BUILD_URL}
Report: ${env.BUILD_URL}artifact/selenium-tests/report.html
""",
                    attachLog: true,
                    attachmentsPattern: 'selenium-tests/report.html'
                )
            }

            sh "${COMPOSE} down || true"
        }
    }
}
