pipeline {
    agent any

    environment {
        APP_URL = 'http://13.63.130.174/'
        COMPOSE = 'docker compose -f docker-compose.yml -p foodapp'
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

                def statsJson = sh(
                    script: '''python3 - <<'PY'
import json
import xml.etree.ElementTree as ET

path = "selenium-tests/results.xml"
def parse_attrs(elem):
    tests = int(elem.attrib.get("tests", 0))
    failures = int(elem.attrib.get("failures", 0))
    errors = int(elem.attrib.get("errors", 0))
    skipped = int(elem.attrib.get("skipped", 0))
    return tests, failures + errors, skipped

try:
    tree = ET.parse(path)
    root = tree.getroot()
    if root.tag == "testsuites":
        total = failed = skipped = 0
        for suite in root.findall("testsuite"):
            t, f, s = parse_attrs(suite)
            total += t
            failed += f
            skipped += s
    else:
        total, failed, skipped = parse_attrs(root)
    passed = max(total - failed - skipped, 0)
    print(json.dumps({"total": total, "failed": failed, "skipped": skipped, "passed": passed}))
except Exception:
    print(json.dumps({"total": 0, "failed": 0, "skipped": 0, "passed": 0}))
PY''',
                    returnStdout: true
                ).trim()

                def stats = new groovy.json.JsonSlurperClassic().parseText(statsJson)
                int total = stats.total as int
                int failures = stats.failed as int
                int skipped = stats.skipped as int
                int passed = stats.passed as int

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
