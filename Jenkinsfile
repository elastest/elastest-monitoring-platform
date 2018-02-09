node('docker')
{
    stage "Container Prep"
        echo("The node is up")
        def mycontainer = docker.image('elastest/ci-docker-siblings:latest')
        mycontainer.pull()
        mycontainer.inside("-u jenkins -v /var/run/docker.sock:/var/run/docker.sock:rw")
        {
            git 'https://github.com/elastest/elastest-monitoring-platform.git'
	    
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

            stage "Build Docker agent"
            	echo ("Building")
            	sh 'cd sentinel-agents/dockerstats'
            	def myimage2 = docker.build 'elastest/emp-docker-agent:latest'

            stage "Publish Docker agent"
                echo ("Publishing")
                //this is work arround as withDockerRegistry is not working properly
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'elastestci-dockerhub',
                    usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']])
                {
                    sh 'docker login -u "$USERNAME" -p "$PASSWORD"'
                    myimage2.push()
                }
        }
}
