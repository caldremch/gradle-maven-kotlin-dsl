

plugins.apply(MavenPublishPlugin::class)


var userName: String? = null
var apiKey: String? = null

val properties = java.util.Properties()
if (project.rootProject.file("local.properties").exists()) {
    properties.load(project.rootProject.file("local.properties").inputStream())
    userName = properties.getProperty("bintray.user")
    apiKey = properties.getProperty("bintray.apiKey")
}

val myBintrayName: String? by extra
val myBintrayName: String? by extra
val myLibraryVersion: String? by project
val myBintrayRepo: String? by project
val myLibraryDescription: String? by project
val myGitUrl: String? by project
val myAllLicenses: String? by project
val myPublishedGroupId: String? by extra
val myLibraryName: String? by project
val myArtifactId: String? by project
val myLicenseName: String? by project
val myLicenseUrl: String? by project
val myDeveloperId: String? by project
val myDeveloperName: String? by project
val myDeveloperEmail: String? by project
val mySiteUrl: String? by project

val isAndroid = project.hasProperty("android")

val myMavenUrl = properties.getProperty("myMavenUrl")
val myMavenUserName = properties.getProperty("myMavenUserName")
val myMavenPassword = properties.getProperty("myMavenPassword")

fun println(log: String) {
    kotlin.io.println("mavenCentral-publish > $log")
}

val myPublishedGroupId1: String? by rootProject.extra
println("${project.hasProperty("android")}")
println("${project.hasProperty("java")}")
println("myPublishedGroupId1=${myPublishedGroupId1}  ${rootProject.extra.getProperty("signing.keyId")}")
//var sourcesJar: TaskProvider<Jar>
if (project.hasProperty("android")) {
    println("is android ....${myPublishedGroupId} ${project.properties["myPublishedGroupId"]}")
//    println("${project.extensions["android"] is BaseE}")
//    val android:Any by project.extensions
    //register sourcesJar for android
//    val a = sourceSets.getByName("main")

//    val sourcesJar = tasks.register("sourcesJar", Jar::class) {
//        archiveClassifier.set("sources")
//        from(sourceSets.getByName("main").java.srcDirs)
//    }
//    //register task javadoc for android
//    val javadoc = tasks.register("javadoc", Javadoc::class) {
//        setSource(sourceSets.getByName("main").java.srcDirs)
////        classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))
//    }
//
//    tasks.register("androidJavaDocsJar", Jar::class) {
//        archiveClassifier.set("javadoc")
//        dependsOn(javadoc)
//        from(javadoc.get().destinationDir)
//    }


} else {

    configure<JavaPluginExtension> {
        withSourcesJar()
//        withJavadocJar()
    }

    tasks.register("javadocJar", Jar::class) {
        val javadoc by tasks
        archiveClassifier.set("javadoc")
        val dirs = (javadoc as Javadoc).destinationDir
        println("javadoc.destinationDir-->${dirs?.absolutePath}")
        from(dirs)
    }


}
val publicationName = "Publication"
configure<PublishingExtension> {

    publications {

        create<MavenPublication>(publicationName) {

            //https://docs.gradle.org/current/userguide/publishing_maven.html
            //官方规定必须加上afterEvaluate,否则上传出现unspecified
            afterEvaluate {
                groupId = myPublishedGroupId
                artifactId = myArtifactId
                version = myLibraryVersion ?: "unspecified-version"

                if (isAndroid) {
                    if (components.size > 0) {
                        val androidJavaDocsJar =  tasks.findByName("androidJavaDocsJar")
                        val sourcesJar = tasks.findByName("sourcesJar")
                        androidJavaDocsJar?.apply {
                            artifact(this)
                        }
                        sourcesJar?.apply {
                            artifact(this)
                        }
                        from(components["debug"])
                    }
                } else {
                    from(components["java"])
                    val javadocJar by tasks
                    artifact(javadocJar)
                }
            }

            pom {
                name.set(myLibraryName)
                description.set(myLibraryDescription)
                url.set(mySiteUrl)
                licenses {
                    license {
                        name.set(myLicenseName)
                        url.set(myLicenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(myDeveloperId)
                        name.set(myDeveloperName)
                        email.set(myDeveloperEmail)
                    }
                }
                scm {
                    connection.set(myGitUrl)
                    developerConnection.set(myGitUrl)
                    url.set(mySiteUrl)
                }
            }

        }
    }

}
