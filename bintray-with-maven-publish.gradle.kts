import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
        classpath("com.android.tools.build:gradle:4.0.1")
    }
}


plugins.apply(BintrayPlugin::class)
plugins.apply(MavenPublishPlugin::class)

var userName: String? = null
var apiKey: String? = null

val properties = java.util.Properties()
if (project.rootProject.file("local.properties").exists()) {
    properties.load(project.rootProject.file("local.properties").inputStream())
    userName = properties.getProperty("bintray.user")
    apiKey = properties.getProperty("bintray.apiKey")
}

val myBintrayName: String? by project
val myLibraryVersion: String? by project
val myBintrayRepo: String? by project
val myLibraryDescription: String? by project
val myGitUrl: String? by project
val myAllLicenses: String? by project
val myPublishedGroupId: String? by project
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
    kotlin.io.println("maven-publish-bintray > $log")
}

//var sourcesJar: TaskProvider<Jar>
if (project.hasProperty("android")) {
    val android = project.extensions["android"] as com.android.build.gradle.BaseExtension
    //register sourcesJar for android
    val sourcesJar = tasks.register("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }
    //register task javadoc for android
    val javadoc = tasks.register("javadoc", Javadoc::class) {
        setSource(android.sourceSets.getByName("main").java.srcDirs)
        classpath += project.files(android.bootClasspath.joinToString(File.pathSeparator))
    }

    tasks.register("androidJavaDocsJar", Jar::class) {
        archiveClassifier.set("javadoc")
        dependsOn(javadoc)
        from(javadoc.get().destinationDir)
    }

} else {

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
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
                        val androidJavaDocsJar by tasks
                        val sourcesJar by tasks
                        artifact(sourcesJar)
                        artifact(androidJavaDocsJar)
                        from(components["debug"])
                    }
                } else {
                    from(components["java"])
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

afterEvaluate {
    configure<BintrayExtension> {
        user = userName
        key = apiKey
        setPublications(publicationName)
        pkg.apply {
            repo = myBintrayRepo
            name = myBintrayName
            desc = myLibraryDescription
            websiteUrl = mySiteUrl
            vcsUrl = myGitUrl
            setLicenses(myAllLicenses)
            publicDownloadNumbers = true
            publish = true
            version.apply {
                desc = myLibraryDescription
            }
        }
    }

}
