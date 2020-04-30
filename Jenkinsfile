node 
{
   try
   {
      jdk = tool name: 'jdk11'
      env.JAVA_HOME = "${jdk}"

      stage 'checkout project'
         checkout scm

      stage 'test'
         sh "mvn test"
         step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

      stage 'package'
         sh "mvn package -DskipTests"
         step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
   } finally
   {
      
   }
}
