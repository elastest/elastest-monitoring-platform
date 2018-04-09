node('docker')
{
    stage "Container Prep for emp"
        echo("The node is up")
        def mycontainer = docker.image('elastest/ci-docker-siblings:latest')
        mycontainer.pull()
        mycontainer.inside("-u jenkins -v /var/run/docker.sock:/var/run/docker.sock:rw")
        {
            git 'https://github.com/elastest/elastest-monitoring-platform.git'

            stage ("Setup") {
            	try
            	{
            		sh "docker fm -f influx"
            	} catch(e) {
            		echo "Error: $e"
            	}

            	sh "docker run -p 8086:8086 --name influx -d --rm influxdb:1.2.4-alpine"
            }
	    
            stage "Tests"
                echo ("Starting tests")
                sh 'mvn clean test'
                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

            stage "Package"
                echo ("Packaging")
                sh 'mvn package -DskipTests'

            stage "Archive atifacts"
                archiveArtifacts artifacts: 'target/*.jar'

            stage "Build image - Package"
                echo ("Building")
                def myimage = docker.build 'elastest/emp:latest'

            stage "Run image"
                myimage.run()

            stage "Publish"
                echo ("Publishing")
                //this is work arround as withDockerRegistry is not working properly
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'elastestci-dockerhub',
                    usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']])
                {
                    sh 'docker login -u "$USERNAME" -p "$PASSWORD"'
                    myimage.push()
                }
        }
}
