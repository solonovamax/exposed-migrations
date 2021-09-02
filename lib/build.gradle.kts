plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    compileOnly(platform("org.jetbrains.kotlin:kotlin-bom"))
    
    // Logging
    compileOnly("org.slf4j:slf4j-api:1.7.30")
    
    implementation("ca.solo-studios:slf4k:0.3.1")
    
    api("org.reflections:reflections:0.9.12")
    
    // Exposed ORM
    val exposedVersion = "0.31.1"
    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "8"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                                     )
        }
    }
}

java {
    withSourcesJar()
}

publishing {
    val libVersion = "3.1.1"
    
    publications {
        create<MavenPublication>("defaultJar") {
            groupId = "com.github.suwayomi"
            artifactId = "exposed-migrations"
            version = libVersion
            
            from(components["kotlin"])
        }
        
        create<MavenPublication>("sourcesJar") {
            groupId = "com.github.suwayomi"
            artifactId = "exposed-migrations"
            version = "$libVersion-sources"
            artifact(tasks.named("sourcesJar"))
            
            from(components["kotlin"])
        }
    }
}