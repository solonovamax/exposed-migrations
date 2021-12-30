import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    signing
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.5.0"
}

group = "gay.solonovamax"
version = "4.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    compileOnly(platform(kotlin("bom")))
    
    // Logging
    api("ca.solo-studios:slf4k:0.4.6")
    
    api("org.reflections:reflections:0.10.2")
    
    // Exposed ORM
    val exposedVersion = "0.36.1"
    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    
    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val dokkaHtml by tasks.getting(DokkaTask::class)

val javadoc by tasks.getting(Javadoc::class)

val jar by tasks.getting(Jar::class)

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(sourcesJar)
            artifact(javadocJar)
            artifact(jar)
            
            version = version as String
            groupId = group as String
            artifactId = "exposed-migrations"
            
            pom {
                name.set("Exposed Migrations")
                description.set("A scuffed utility library for jetbrains Exposed to support migrations")
                url.set("https://github.com/solonovamax/exposed-migrations")
                
                inceptionYear.set("2021")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://mit-license.org/")
                    }
                }
                developers {
                    developer { // Original developer (not in commit history)
                        id.set("andreas-mausch")
                        name.set("Andreas Mausch")
                        email.set("neonew@gmail.com")
                        url.set("https://andreas-mausch.de/")
                    }
                    developer { // Committer on the Suwayomi fork
                        id.set("Aria Moradi")
                        name.set("Aria Moradi")
                        email.set("aria.moradi007@gmail.com")
                        url.set("https://github.com/solonovamax/")
                    }
                    developer {
                        id.set("solonovamax")
                        name.set("solonovamax")
                        email.set("solonovamax@12oclockpoint.com")
                        url.set("https://github.com/solonovamax/")
                    }
                }
                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/solonovamax/exposed-migrations/issues")
                }
                scm {
                    connection.set("scm:git:https://github.com/solonovamax/exposed-migrations.git")
                    developerConnection.set("scm:git:ssh://github.com/solonovamax/exposed-migrations.git")
                    url.set("https://github.com/solonovamax/exposed-migrations/")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "sonatypeStaging"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials(org.gradle.api.credentials.PasswordCredentials::class)
        }
        maven {
            name = "sonatypeSnapshot"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
