pipeline {
  agent any
  options { timestamps() }

  environment {
    SONARQUBE_ENV = 'sonarqube'

    HARBOR_REGISTRY = 'harbor.cicd.kmg22.me'
    HARBOR_PROJECT  = 'cicd'
    APP_NAME        = 'cicd-app'

    GITOPS_REPO_URL = 'https://github.com/kmg22/CICD-gitops.git'
    GITOPS_VALUES   = 'apps/cicd-app/values.yaml'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test (Testcontainers)') {
      steps {
        sh '''
          chmod +x gradlew
          ./gradlew clean build jacocoTestReport --no-daemon
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

    stage('Docker Build & Push to Harbor') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'harbor-creds', usernameVariable: 'H_USER', passwordVariable: 'H_PASS')]) {
          sh '''
            set -eux
            IMAGE=${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${APP_NAME}:${GIT_COMMIT}

            docker login ${HARBOR_REGISTRY} -u "$H_USER" -p "$H_PASS"
            docker build -t $IMAGE .
            docker push $IMAGE

            docker tag $IMAGE ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${APP_NAME}:latest
            docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${APP_NAME}:latest
          '''
        }
      }
    }

    stage('Update GitOps repo') {
      steps {
        withCredentials([string(credentialsId: 'gitops-token', variable: 'GITOPS_TOKEN')]) {
          sh '''
            set -eux
            rm -rf cicd-gitops
            git clone https://$GITOPS_TOKEN@github.com/kmg22/CICD-gitops.git cicd-gitops
            cd cicd-gitops

            # values.yaml의 tag를 커밋 해시로 변경
            sed -i "s/^  tag: .*/  tag: ${GIT_COMMIT}/" ${GITOPS_VALUES}

            git config user.email "ssukmg22@gmail.com"
            git config user.name "kmg22"

            git add ${GITOPS_VALUES}
            git commit -m "chore(gitops): deploy ${APP_NAME} ${GIT_COMMIT}" || echo "no changes"
            git push origin main
          '''
        }
      }
    }
  }
}
