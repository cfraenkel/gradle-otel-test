plugins {
  id("com.atkinsondev.opentelemetry-build") version "3.0.0"
  java
}

val jaegerUrl = "http://localhost:4317"

openTelemetryBuild {
  endpoint.set(jaegerUrl)
  taskTraceEnvironmentEnabled.set(true)
}

val runDocker by tasks.registering(Exec::class) {
  commandLine("docker", "buildx", "build", ".")
  environment("OTEL_EXPORTER_OTLP_ENDPOINT", jaegerUrl)
  doFirst {
    val traceId = environment["TRACE_ID"]
    val spanId = environment["SPAN_ID"]
    environment("TRACEPARENT", "00-$traceId-$spanId-01")
  }
}
dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
repositories {
  mavenCentral()
}
tasks.build { dependsOn(runDocker) }
tasks.test {
  useJUnitPlatform()
}