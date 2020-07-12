
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    id("maven-publish")
    id("io.gitlab.arturbosch.detekt") version "1.1.1"
    jacoco
    `java-library`
}

group = "com.marcinziolo"
version = "1.0.0"

repositories {
    mavenLocal()
    jcenter()
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
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