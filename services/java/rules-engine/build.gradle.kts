plugins {
    `java-library`
}

dependencies {
    api(project(":shared"))
    api("org.drools:drools-core:9.44.0.Final")
    api("org.drools:drools-compiler:9.44.0.Final")
    api("org.drools:drools-mvel:9.44.0.Final")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
