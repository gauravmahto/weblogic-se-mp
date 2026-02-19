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

1. **JSON handling** - Add Jackson or JSON-B ✅ (Completed in Phase 1.2)
2. **Database** - H2 for dev, PostgreSQL for prod
3. **Configuration** - application.yaml files
4. **Error handling** - Custom error responses ✅ (Completed in Phase 1.2)
5. **Validation** - Input validation ✅ (Completed in Phase 1.2)
6. **Testing** - REST Assured for API tests
7. **Metrics** - Prometheus endpoint
8. **Tracing** - Jaeger integration

---

## Phase 1.2: JSON Handling & CRUD Implementation (Feb 19, 2026)

### The Big Picture

Built a complete **user management REST API** with:

- Full CRUD operations (Create, Read, Update, Delete)
- JSON request/response handling
- Query parameter filtering
- Request validation
- Consistent error responses
- All tested locally and in a Docker container

### Core Implementation Patterns

#### 1. JSON-B Serialization (The Magic)

**Dependency**: `helidon-http-media-jsonb`

**How it works:**

```java
// Incoming JSON string automatically becomes User object
User user = req.content().as(User.class);

// User object automatically becomes JSON string
res.send(user);  // or res.status(Status.CREATED_201).send(user);
```

**Why this is powerful:**

- No manual JSON parsing/generation
- Standard POJO with getter/setter works out of the box
- Default constructor required (`public User() {}`)
- Content-Type: application/json handled automatically

#### 2. Thread-Safe In-Memory Storage

```java
private final Map<String, User> users = new ConcurrentHashMap<>();
private final AtomicLong idGenerator = new AtomicLong(1);
```

**Why ConcurrentHashMap?**

- Multiple concurrent HTTP requests hit the same map
- No race conditions or corruption
- Better than synchronized methods

**Why AtomicLong?**

- ID generation must be atomic
- `getAndIncrement()` is thread-safe
- Prevents duplicate IDs

**ID Collision Avoidance:**

```java
private void trackId(String id) {
    try {
        long numericId = Long.parseLong(id);
        idGenerator.updateAndGet(current -> Math.max(current, numericId + 1));
    } catch (NumberFormatException ignored) {}
}
```

- When seeding users 1, 2 → auto-generated starts at 3
- Prevents duplicates when mixing seeded + auto-generated IDs

#### 3. Query Parameter Filtering

```java
String name = req.query().first("name").orElse(null);
String email = req.query().first("email").orElse(null);
```

**Query filtering implementation:**

```java
public List<User> findUsers(String name, String email) {
    String nameFilter = normalizeFilter(name);      // lowercase + trim
    String emailFilter = normalizeFilter(email);
    List<User> results = new ArrayList<>();
    for (User user : users.values()) {
        if (matchesFilter(user.getName(), nameFilter) &&
            matchesFilter(user.getEmail(), emailFilter)) {
            results.add(user);
        }
    }
    return results;
}
```

**Examples:**

- `GET /users` → all users
- `GET /users?name=alice` → users containing "alice"
- `GET /users?email=@example.com` → users from domain
- `GET /users?name=alice&email=@example.com` → both conditions (AND logic)

#### 4. Request Validation Before Persistence

```java
rules.post("/users", (req, res) -> {
    User user = readUser(req, res);        // Parse JSON
    if (user == null) return;              // Error already sent

    String error = validateUser(user);     // Business rules
    if (error != null) {
        sendError(res, BAD_REQUEST_400, "Validation error", error);
        return;
    }

    User created = userService.createUser(user);
    res.status(CREATED_201).send(created);
});
```

**Validation checks:**

```java
private static String validateUser(User user) {
    if (user == null) return "User payload is required.";
    if (isBlank(user.getName())) return "name is required.";
    if (isBlank(user.getEmail())) return "email is required.";
    if (!user.getEmail().contains("@")) return "email must contain '@'.";
    return null;  // Passes all checks
}
```

#### 5. Consistent JSON Error Responses

```java
public class ErrorResponse {
    private String message;    // Error type
    private String details;    // Details
}
```

**Usage:**

```java
sendError(res, BAD_REQUEST_400, "Invalid JSON", "Request body must be valid JSON.");
sendError(res, NOT_FOUND_404, "User not found", "No user with id: 999");
```

**Why two fields?**

- `message` - for UI display
- `details` - for debugging

### HTTP Status Codes in Action

| Code | Meaning      | When to Use                     |
| ---- | ------------ | ------------------------------- |
| 200  | OK           | GET, POST, PUT successful       |
| 201  | Created      | POST returns new resource       |
| 204  | No Content   | DELETE successful (empty body)  |
| 400  | Bad Request  | Invalid JSON, validation failed |
| 404  | Not Found    | Resource doesn't exist          |
| 500  | Server Error | Unexpected exception            |

### Path vs Query Parameters

**Path parameters** (resource ID):

```java
GET /users/42
String id = req.path().pathParameters().get("id");  // "42"
```

**Query parameters** (filters/options):

```java
GET /users?name=Alice&page=2
String name = req.query().first("name").orElse(null);
```

**Convention:**

- Path: Required, identifies resource
- Query: Optional, filters or options

### Common Testing Patterns

**Happy path (CRUD cycle):**

```bash
# Create
curl -X POST -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com"}' \
  http://localhost:8080/users

# Read
curl http://localhost:8080/users/3

# Update
curl -X PUT -H "Content-Type: application/json" \
  -d '{"name":"Updated","email":"new@example.com"}' \
  http://localhost:8080/users/3

# Delete
curl -X DELETE http://localhost:8080/users/3
```

**Error cases:**

```bash
# Invalid JSON
curl -X POST -H "Content-Type: application/json" \
  -d 'invalid' http://localhost:8080/users

# Missing field
curl -X POST -H "Content-Type: application/json" \
  -d '{"name":"NoEmail"}' http://localhost:8080/users

# Not found
curl http://localhost:8080/users/999
```

### Key Insights from Phase 1.2

1. **Functional routing is explicit** - easy to trace request flow
2. **JSON-B is transparent** - POJO in/out, minimal code
3. **ConcurrentHashMap is essential** - production requires thread safety
4. **Validation prevents bad data** - never trust client input
5. **Structured errors improve debugging** - JSON errors > plain text
6. **HTTP semantics matter** - 201 vs 200 conveys meaningful info
7. **Container testing validates real-world** - Docker ensures it works

### Common Gotchas

❌ **Using HashMap** (not thread-safe)
❌ **Forgetting default constructor** (JSON-B needs it)
❌ **Skipping validation** (corrupts data)
❌ **Generic error messages** (hard to debug)

### Upcoming in Phase 1.2 Remaining

- Integration tests (automate CRUD testing)
- Filters/Interceptors (logging, tracing)
- CORS configuration
- Static content serving
- Then Phase 1.3: Configuration management
