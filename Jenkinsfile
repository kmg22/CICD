pipeline {
  agent any

  environment {
    SONARQUBE_ENV = 'sonarqube'
  }

  options {
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test (Testcontainers)') {
      steps {
        sh '''
          chmod +x gradlew
          ./gradlew clean test jacocoTestReport --no-daemon
        '''
      }
      post {
        always {
          junit '**/build/test-results/test/*.xml'
          archiveArtifacts artifacts: 'build/reports/jacoco/test/**', allowEmptyArchive: true
        }
      }
    }

    stage('SonarQube Scan') {
      steps {
        withSonarQubeEnv("${SONARQUBE_ENV}") {
          sh './gradlew sonar --no-daemon'
        }
      }
    }
  }
}
