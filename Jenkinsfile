def branch = BRANCH_NAME

node('master') {
    stage('Prepare') {
        checkout scm
        sh "./gradlew clean"
    }

    stage('Build') {
        sh "./gradlew build"
    }

    stage('Test') {
        sh "./gradlew test"
        junit "build/test-results/test/*.xml"
    }
}