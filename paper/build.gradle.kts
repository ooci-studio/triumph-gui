plugins {
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:21.0.1")
    api(project(":triumph-gui")) {
        exclude(group = "net.kyori")
    }
}

license {
    header = rootProject.file("LICENSE")
    encoding = "UTF-8"
    mapping("java", "JAVADOC_STYLE")
    include("**/*.java")
}

val javaComponent: SoftwareComponent = components["java"]

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    disableAutoTargetJvm()
}

tasks {

    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(javaComponent)
                artifact(sourcesJar)
                artifact(javadocJar)
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                pom {
                    name.set("Triumph GUI")
                    description.set("Library for easy creation of GUIs for Bukkit plugins.")
                    url.set("https://github.com/TriumphTeam/triumph-gui")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("http://www.opensource.org/licenses/mit-license.php")
                        }
                    }

                    developers {
                        developer {
                            id.set("matt")
                            name.set("Mateus Moreira")
                            organization.set("TriumphTeam")
                            organizationUrl.set("https://github.com/TriumphTeam")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/TriumphTeam/triumph-gui.git")
                        developerConnection.set("scm:git:ssh://github.com:TriumphTeam/triumph-gui.git")
                        url.set("https://github.com/TriumphTeam/triumph-gui")
                    }
                }
            }
        }

        repositories {
            maven {
                credentials {
                    username = project.providers.gradleProperty("repo.username").get()
                    password = project.providers.gradleProperty("repo.password").get()
                }

                if(version.toString().contains("SNAPSHOT")) {
                    url = uri("https://repo.carljoy.cn/repository/maven-snapshots/")
                    return@maven
                }

                url = uri("https://repo.carljoy.cn/repository/maven-releases/")
            }
        }

    }

    withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
        options.encoding = "UTF-8"
    }
}
