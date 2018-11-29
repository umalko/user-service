pipeline {
  agent {
    docker {
      image 'maven:3.3.3'
    }

  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
        sh 'mvn test'
        sh 'mvn install'
      }
    }
    stage('Deploy Dev') {
      steps {
        echo 'hi!'
      }
    }
  }
}