Apptest.ai Step Plugin
===================

This pipeline plugin provices Apptest.ai test for Jenkins.


Usage
-----

```
node {
    def apkFile
    def accessKey
    def serviceProjectId
    def testResult

    stage('Preparation') {
        echo "Current workspace : ${workspace}"
        accessKey = 'c39d769ad5d65650bb759e3b656dae1d'
        serviceProjectId = 479
    }

    // Apptestai Test Stage
    stage('Apptestai Test') {
        apkFile="${workspace}/app/build/outputs/apk/prod/debug/app-prod-arm64-v8a-debug.apk"

        testResult = testApptestai(accessKey : accessKey, projectId : serviceProjectId, apkFilePath : apkFile)
        echo "testResult : ${testResult}"
    }
    stage('write test result') {
        sh "mkdir -p tmp/"        
        sh "echo -n '${testResult}' > tmp/TESTS-Apptestai.xml"
    }
    stage('junit') {
        junit 'tmp/TESTS-*.xml'
    }
}

```
