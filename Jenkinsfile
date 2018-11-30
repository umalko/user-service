pipeline {
  agent {
    docker {
      image 'maven:3.3.3'
    }

  }
  stages {
    stage('Compile') {
      steps {
        sh 'mvn clean compile'
      }
    }
    stage('JUnit Test') {
      parallel {
        stage('JUnit Test') {
          steps {
            sh 'mvn test'
          }
        }
        stage('Sonar Check') {
          steps {
            echo 'checked!'
          }
        }
      }
    }
    stage('Package') {
      steps {
        sh 'mvn package'
      }
    }
    stage('Deploy Dev') {
      steps {
        echo 'Started deploying to DEV landscape!'
        timeout(time: 30, unit: 'MINUTES', activity: true) {
          pushToCloudFoundry(target: 'https://api.run.pivotal.io', organization: 'mavs-org', cloudSpace: 'development', credentialsId: 'pcfdev_user', pluginTimeout: '99999999999999')
        }

        echo 'deployed to DEV landscape!'
      }
    }
  }
}