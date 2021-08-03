import com.jfrog.bintray.gradle.BintrayExtension
import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.17.1"
    jacoco
    `java-library`
    signing
    `maven-publish`
    id("nu.studer.credentials") version "1.0.7"
    id("com.jfrog.bintray") version "1.8.5"
    id("com.github.ben-manes.versions") version "0.38.0"
}

group = "com.marcinziolo"
version = "1.0.2"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

detekt {
    config = files("$rootDir/detekt.yml")
    reports {
        html.enabled = true
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("com.github.tomakehurst:wiremock:2.27.1")

    testImplementation("io.rest-assured:rest-assured:4.3.0")
    testImplementation("io.rest-assured:json-path:4.3.0")
    testImplementation("io.rest-assured:kotlin-extensions:4.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("io.mockk:mockk:1.10.0")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

jacoco {
    toolVersion = "0.8.7"
}

val credentials: CredentialsContainer by project.extra
val ossrhUser = (System.getProperty("ossrhUser") ?: credentials.getProperty("ossrhUser") ?: "-") as String
val ossrhPassword = (System.getProperty("ossrhPassword") ?: credentials.getProperty("ossrhPassword") ?: "-") as String
val bintrayKey = (System.getProperty("bintrayKey") ?: credentials.getProperty("bintrayKey") ?: "-") as String

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "kotlin-wiremock"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Kotlin Wiremock")
                description.set("Handy Kotlin DSL for Wiremock stubbing")
                url.set("https://github.com/marcinziolo/kotlin-wiremock")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("marcinziolo")
                        name.set("Marcin Zioło")
                        email.set("martin.ziolo@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com:marcinziolo/kotlin-wiremock.git")
                    developerConnection.set("scm:git:ssh://github.com:marcinziolo/kotlin-wiremock.git")
                    url.set("https://github.com/marcinziolo/kotlin-wiremock")
                }
            }
        }
    }
    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUser
                password = ossrhPassword
            }
        }
    }
}

bintray {
    user = ossrhUser
    key = bintrayKey
    publish = true
    setPublications("mavenJava")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "kotlin-wiremock"
        websiteUrl = "https://github.com/marcinziolo/kotlin-wiremock"
        githubRepo = "marcinziolo/kotlin-wiremock"
        vcsUrl = "https://github.com/marcinziolo/kotlin-wiremock"
        description = "Handy Kotlin DSL for Wiremock stubbing"
        setLabels("kotlin")
        setLicenses("Apache-2.0")
        desc = description
    })
}
tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
