# Technical Notes & Insights

## Helidon SE Architecture

### Functional vs. Declarative Style
Helidon SE uses a **functional programming** approach:
```java
// Functional (SE)
rules.get("/hello", (req, res) -> res.send("Hello"));

// vs Declarative (MP - for future reference)
@GET
@Path("/hello")
public String hello() {
    return "Hello";
}
```

**Pros of SE:**
- Lightweight, minimal overhead
- Explicit control flow
- No reflection/annotation processing
- Faster startup time
- Smaller memory footprint

**Cons of SE:**
- More verbose for complex apps
- Less familiar to Java EE developers
- Manual dependency wiring

### Helidon WebServer Components

**Key Classes:**
- `WebServer` - Main server instance
- `HttpRouting` - Routes configuration
- `ServerRequest` - HTTP request wrapper
- `ServerResponse` - HTTP response wrapper
- `Status` - HTTP status codes enum

```java
WebServer server = WebServer.builder()
    .port(8080)
    .routing(Main::routing)  // Method reference
    .build();
```

---

## Gradle Configuration

### Multi-Module Project Structure

**Root `settings.gradle`:**
- Defines project name
- Includes submodules

**Root `build.gradle`:**
- Common configurations
- Plugin management
- Shared dependencies

**Module `app/build.gradle`:**
- Module-specific configurations
- Dependencies
- Custom tasks

### Fat JAR Creation (Uber JAR)

**Why needed?**
- Standard JAR only contains compiled classes
- Dependencies need to be on classpath separately
- Container needs self-contained executable

**Implementation:**
```gradle
tasks.jar {
    manifest {
        attributes 'Main-Class': 'com.acme.Main'
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from {
        configurations.runtimeClasspath.collect { 
            it.isDirectory() ? it : zipTree(it) 
        }
    }
}
```

**What this does:**
1. Sets Main-Class in MANIFEST.MF
2. Extracts all JAR dependencies
3. Bundles everything into single JAR
4. Handles duplicate files (keeps first)

**Alternative approaches:**
- Shadow plugin (better for complex scenarios)
- Spring Boot plugin (for Spring apps)
- Application plugin distributions

---

## Container Best Practices

### Multi-Stage Dockerfile Benefits

**Stage 1 (Build):**
```dockerfile
FROM eclipse-temurin:21-jdk AS build
# Build artifacts here
```
- Full JDK environment
- All build tools (Gradle)
- Source code compilation

**Stage 2 (Runtime):**
```dockerfile
FROM eclipse-temurin:21-jre
# Copy only JAR from build stage
```
- Smaller JRE image (no compiler)
- No source code
- No build tools
- Reduced attack surface

**Image size comparison:**
- JDK image: ~450 MB
- JRE image: ~180 MB
- Multi-stage final: ~200 MB (with app)
- Single-stage: ~470 MB (with app)

### Podman vs Docker

**Key Differences:**
- Podman is daemonless (no background service)
- Rootless by default (better security)
- Pod support built-in (Kubernetes-style)
- Drop-in replacement for Docker CLI

**Common Commands:**
```bash
# Build
podman build -t <image>:<tag> .

# Run
podman run --rm -p 8080:8080 <image>

# List containers
podman ps

# Stop
podman stop <container-id>

# Remove image
podman rmi <image>

# Inspect
podman inspect <container-id>
```

---

## Java 21 Features Used

### Text Blocks (Java 15+)
Useful for multi-line strings, JSON, SQL:
```java
String json = """
    {
        "message": "Hello"
    }
    """;
```

### Records (Java 16+)
Simple data carriers:
```java
public record User(String name, int age) {}
```

### Pattern Matching (Java 21)
Enhanced switch expressions:
```java
return switch (status) {
    case OK -> "Success";
    case ERROR -> "Failed";
    default -> "Unknown";
};
```

---

## Common Issues & Solutions

### Issue: Gradle Daemon Timeouts
**Symptom:** Builds hang or timeout
**Solution:**
```bash
./gradlew --stop  # Kill daemon
./gradlew build --no-daemon  # Run without daemon
```

### Issue: Port Already in Use
**Symptom:** `Address already in use`
**Solutions:**
```bash
# Check what's using port
lsof -i :8080

# Kill process
kill -9 <PID>

# Or use different port
podman run -p 8081:8080 <image>
```

### Issue: Container Logs Not Showing
**Solution:**
```bash
# Run in foreground (removes --rm if needed)
podman run -it -p 8080:8080 <image>

# Check logs of running container
podman logs <container-id>

# Follow logs
podman logs -f <container-id>
```

### Issue: Changes Not Reflected in Container
**Problem:** Forgot to rebuild image
**Solution:**
```bash
# Always rebuild after code changes
podman build -f app/Dockerfile -t helidon-se:1.0.0 .
```

### Issue: Tests Failing in Container but Not Locally
**Common Causes:**
- Different Java versions
- Missing test resources
- Environment variables
- File path differences

**Debug:**
```bash
# Build locally first
./gradlew clean build

# Check test reports
open app/build/reports/tests/test/index.html
```

---

## Performance Considerations

### Startup Time Optimization
- Use JRE instead of JDK in runtime
- Enable Class Data Sharing (CDS)
- Use GraalVM native image (advanced)
- Minimize dependencies

### Memory Optimization
```bash
# Set JVM memory limits
java -Xmx256m -Xms128m -jar app.jar

# In Dockerfile
ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app/app.jar"]
```

### Build Time Optimization
- Use Gradle build cache
- Leverage Docker layer caching
- Use `.dockerignore`
- Parallel builds with Gradle

---

## Development Workflow

### Recommended Local Development Flow
```bash
# 1. Make code changes
vim app/src/main/java/com/acme/Main.java

# 2. Run tests
./gradlew :app:test

# 3. Run locally (quick feedback)
./gradlew :app:run

# 4. Build container (before deployment)
podman build -f app/Dockerfile -t helidon-se:1.0.0 .

# 5. Test container
podman run --rm -p 8080:8080 helidon-se:1.0.0
```

### Fast Feedback Loop
For rapid iteration, prefer:
1. `./gradlew :app:run` - fastest
2. Manual `java -jar` - fast
3. Container rebuild - slower but production-like

---

## Security Notes

### Container Security
- ✅ Use official base images (eclipse-temurin)
- ✅ Multi-stage builds (no build tools in prod)
- ⏳ Run as non-root user (TODO)
- ⏳ Scan images for vulnerabilities (TODO)
- ⏳ Sign images (TODO)

### Application Security (Future)
- Input validation
- Rate limiting
- Authentication/Authorization
- HTTPS/TLS
- Secret management
- CORS configuration

---

## Useful Commands Reference

### Gradle
```bash
# Build
./gradlew build

# Clean build
./gradlew clean build

# Run tests only
./gradlew test

# Run application
./gradlew :app:run

# List tasks
./gradlew tasks

# Show dependencies
./gradlew :app:dependencies

# Refresh dependencies
./gradlew --refresh-dependencies
```

### Podman
```bash
# Build with context
podman build -f <dockerfile> -t <tag> <context>

# Run with port mapping
podman run --rm -p <host>:<container> <image>

# Run in background
podman run -d -p 8080:8080 <image>

# Execute command in container
podman exec -it <container> /bin/bash

# Copy files from container
podman cp <container>:/path/to/file ./local

# Save image to tar
podman save -o image.tar <image>

# Load image from tar
podman load -i image.tar
```

### Testing Endpoints
```bash
# Simple GET
curl http://localhost:8080/hello

# With headers
curl -H "Content-Type: application/json" http://localhost:8080/api

# POST with JSON
curl -X POST -H "Content-Type: application/json" \
  -d '{"name":"test"}' http://localhost:8080/api

# Pretty print JSON response
curl http://localhost:8080/api | jq .
```

---

## Next Learning Topics (Quick Reference)

1. **JSON handling** - Add Jackson or JSON-B
2. **Database** - H2 for dev, PostgreSQL for prod
3. **Configuration** - application.yaml files
4. **Error handling** - Custom error responses
5. **Validation** - Input validation
6. **Testing** - REST Assured for API tests
7. **Metrics** - Prometheus endpoint
8. **Tracing** - Jaeger integration
