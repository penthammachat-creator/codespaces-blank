plugins {
    application
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("checkstyle")
}
java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }
repositories { mavenCentral() }
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
}
application { mainClass.set("phx.App") }
javafx { version = "22.0.1"; modules = listOf("javafx.controls","javafx.graphics") }
tasks.test { useJUnitPlatform() }
checkstyle {
    toolVersion = "10.12.5"
    config = resources.text.fromString("""
    <!DOCTYPE module PUBLIC "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
      "https://checkstyle.org/dtds/configuration_1_3.dtd">
    <module name="Checker">
      <module name="TreeWalker">
        <module name="WhitespaceAfter"/>
        <module name="WhitespaceAround"/>
        <module name="NeedBraces"/>
      </module>
    </module>
    """.trimIndent())
}
