pipeline {
    agent any  
    
    environment {
         IMAGE_NAME = "some-image.lindroos.eu/taas/teams-service:${BUILD_ID}-${GIT_COMMIT}"
         CI_SERVICE_NAME = "taas-teams-service"
    }
    stages { 
        stage('Maven Build') { 
            agent {
                docker {
                    image 'maven:3.6-jdk-11'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                /*script{
                    // Clean workspace
                    sh 'git clean -fdx'
                }*/
                script{
                    withCredentials([file(credentialsId: 'some-maven-settings.xml', variable: 'FILE')]) {
                        sh 'mvn --settings $FILE clean deploy -U -DskipTests'
                        stash includes: 'service/target/*.jar', name: 'service-jars'
                    }
                }
            }
        }
        stage ("Docker image build & publish"){
            steps{
                script{
                    unstash 'service-jars'
                    def dockerImage = docker.build("${IMAGE_NAME}", '-f dockerfile .')
                    docker.withRegistry("https://some-image.lindroos.eu/taas/teams-service", 'some-creds-in-jenkins'){
                        dockerImage.push();
                        dockerImage.push('latest');
                    }
                }
            }    
        }
        stage ("CI Update"){
            steps {
                build job: '../upgrade-ci-env/master', parameters: [
                    [ $class: 'StringParameterValue', name: 'imageVersion', value: "${env.BUILD_NUMBER}-${env.GIT_COMMIT}" ],
                    [ $class: 'StringParameterValue', name: 'imageName', value: "teams-service" ]
                ]
            }
        }
    }       

}