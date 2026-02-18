# WebLogic to Helidon Migration Learning Project

## Project Overview
This project is a hands-on learning journey to understand:
- **Java Helidon framework** (SE → MP progression)
- **Gradle** build system
- **Container deployment** with Podman
- **WebLogic Server** integration
- **Cloud-native application** development

## Current Status: Helidon SE Basics ✅

### What's Working
- ✅ Simple Helidon SE application with REST endpoints
- ✅ Gradle build configuration with fat JAR packaging
- ✅ Containerized deployment with Podman
- ✅ Multi-stage Dockerfile for optimized images
- ✅ JUnit 5 test setup

## Project Structure
```
weblogic-se-mp/
├── app/
│   ├── src/
│   │   ├── main/java/com/acme/
│   │   │   └── Main.java          # Helidon SE application
│   │   └── test/java/com/acme/
│   │       └── MainTest.java      # Unit tests
│   ├── build.gradle               # App module build config
│   └── Dockerfile                 # Container image definition
├── build.gradle                   # Root build config
├── settings.gradle                # Multi-module setup
└── gradlew                        # Gradle wrapper
```

## Quick Start

### Run Locally with Gradle
```bash
./gradlew :app:run
```

### Run Tests
```bash
./gradlew :app:test
```

### Build and Run Container
```bash
# Build the container image
podman build -f app/Dockerfile -t helidon-se:1.0.0 .

# Run the container
podman run --rm -p 8080:8080 helidon-se:1.0.0

# Test the endpoints
curl http://localhost:8080/hello
curl http://localhost:8080/health
curl http://localhost:8080/echo/test
```

## Available Endpoints
- `GET /hello` - Simple greeting message
- `GET /health` - Health check endpoint
- `GET /echo/{msg}` - Echo service that returns your message

## Technology Stack
- **Java 21** (Eclipse Temurin)
- **Helidon SE 4.1.5** (lightweight, functional style)
- **Gradle 8.14.2** (build automation)
- **JUnit 5** (testing)
- **Podman** (container runtime)

## Documentation
- [Learning Plan](docs/PLAN.md) - Complete learning roadmap
- [Progress Log](docs/PROGRESS.md) - Detailed progress notes
- [Technical Notes](docs/NOTES.md) - Technical insights and troubleshooting
- [Learning Goals](docs/GOALS.md) - Current state and objectives
