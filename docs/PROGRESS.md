# Progress Log

## Session 1: Initial Setup & Containerization (Feb 18, 2026)

### ✅ Completed Tasks

#### 1. Project Investigation

- Analyzed existing Gradle multi-module project structure
- Identified Helidon SE 4.1.5 application with basic REST endpoints
- Reviewed test setup with JUnit 5

#### 2. Troubleshooting & Fixes

**Issue #1: Podman Build Failed**

- **Problem**: Running `podman build` from `app/` directory
- **Error**: `copier: stat: "/gradle": no such file or directory`
- **Root Cause**: Dockerfile expects to run from project root where `gradle/` directory exists
- **Solution**: Run build from project root with `-f` flag:
  ```bash
  podman build -f app/Dockerfile -t helidon-se:1.0.0 .
  ```

**Issue #2: Container Failed to Start**

- **Problem**: Container exits with code 1
- **Error**: `no main manifest attribute, in /app/app.jar`
- **Root Cause**: Standard JAR doesn't include:
  - Main-Class manifest attribute
  - Runtime dependencies
- **Solution**: Modified `app/build.gradle` to create fat JAR:
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

#### 3. Successful Deployment

- ✅ Built container image: `helidon-se:1.0.0`
- ✅ Container runs successfully on port 8080
- ✅ All endpoints responding correctly:
  - `/hello` → "Hello from Helidon SE"
  - `/health` → "OK" (200 status)
  - `/echo/{msg}` → echoes back the message

### Key Learnings

#### Gradle Fat JAR Creation

- Use `jar` task with custom configuration
- Include all runtime dependencies with `zipTree()`
- Set `Main-Class` in manifest
- Handle duplicates with `DuplicatesStrategy.EXCLUDE`

#### Docker Multi-Stage Builds

- Stage 1: Build environment with JDK
- Stage 2: Runtime environment with JRE (smaller image)
- Benefits: Reduced image size, no build tools in production

#### Podman Best Practices

- Always run build from correct context directory
- Use `-f` flag to specify Dockerfile location
- Use `--rm` flag for automatic cleanup

### Files Modified

- `app/build.gradle` - Added fat JAR configuration

### Files Created

- `.gitignore` - Standard Java/Gradle ignores
- `docs/HOW_TO_USE.md` - Comprehensive usage guide (⭐ IMPORTANT)
- `docs/BRANCHING.md` - Git workflow strategy
- `QUICKREF.md` - Quick command reference

### Documentation Structure

All learning documentation now organized in `docs/` directory:

- `HOW_TO_USE.md` - Your primary guide for using this project
- `PLAN.md` - Complete learning roadmap
- `PROGRESS.md` - This file
- `GOALS.md` - Current state tracking
- `NOTES.md` - Technical insights
- `BRANCHING.md` - Git strategy

**🎯 Mental Note**: Always update `docs/HOW_TO_USE.md` when you discover better workflows or practices. This is your living guide!

### Next Steps

- Document learning goals and plan
- Explore Helidon SE features in depth
- Plan WebLogic Server integration
- Progress to Helidon MP

---

## Session 2: Helidon SE JSON + CRUD (Feb 19, 2026)

### ✅ Completed Tasks

#### 1. JSON Handling and CRUD API

- Added JSON-B media support for request/response bodies
- Created `User` entity and in-memory `UserService`
- Implemented CRUD endpoints for `/users`

#### 2. Validation and Error Handling

- Added JSON error payloads for consistent API failures
- Implemented request validation for required fields
- Added invalid JSON handling (400 responses)

#### 3. Routing Enhancements

- Added query-parameter filtering for `/users` (name/email)
- Confirmed path parameters for `/users/{id}` and `/echo/{msg}`

### Key Learnings

#### Helidon SE Request Handling

- `req.content().as(User.class)` deserializes JSON using JSON-B
- Query params available via `req.query().first("name")`
- Consistent error payloads improve API clarity

### Files Modified

- `app/src/main/java/com/acme/Main.java`
- `app/src/main/java/com/acme/UserService.java`

### Files Created

- `app/src/main/java/com/acme/ErrorResponse.java`
- `app/src/main/java/com/acme/User.java`

#### 4. Complete CRUD Implementation

- GET /users - List all users
- GET /users/{id} - Fetch by ID (returns 404 if not found)
- POST /users - Create new user (returns 201 Created)
- PUT /users/{id} - Update existing user
- DELETE /users/{id} - Delete user (returns 204 No Content)
- GET /users?name=X&email=Y - Query filtering

#### 5. Comprehensive Testing

- Built and tested via Dockerfile (multi-stage)
- Validated all CRUD operations with curl
- HTTP status codes verified:
  - 200 OK for successful GET/POST/PUT
  - 201 Created for POST
  - 204 No Content for DELETE
  - 400 Bad Request for invalid JSON and validation failures
  - 404 Not Found for missing resources
- Query parameter filtering verified
- Error responses include `message` and `details` fields
- Container image runs successfully on port 8080

### Architecture & Code Patterns Learned

#### 1. User Entity (POJO for JSON Binding)

```java
public class User {
    private String id, name, email;
    public User() {}  // Required for JSON deserialization
    // getters/setters enabled via POJO pattern
}
```

- Default constructor required for JSON-B binding
- Getters/setters enable auto-binding
- JSON-B handles serialization transparently

#### 2. UserService (In-Memory Data Store)

```java
private final Map<String, User> users = new ConcurrentHashMap<>();
private final AtomicLong idGenerator = new AtomicLong(1);
```

- Thread-safe storage with ConcurrentHashMap
- Auto-incrementing ID avoiding collisions with seeded data
- Query filtering with case-insensitive substring matching
- Methods: getAllUsers(), getUserById(id), createUser(), updateUser(), deleteUser(), findUsers()

#### 3. Query Parameter Parsing (Routing)

```java
String name = req.query().first("name").orElse(null);
String email = req.query().first("email").orElse(null);
var users = (isBlank(name) && isBlank(email))
    ? userService.getAllUsers()
    : userService.findUsers(name, email);
```

- Optional query params with graceful null handling
- Filters applied only when params provided

#### 4. Request Validation

```java
String validationError = validateUser(user);
if (validationError != null) {
    sendError(res, Status.BAD_REQUEST_400, "Validation error", validationError);
    return;
}
```

- Centralized validation before persistence
- Detailed error messages returned to client

#### 5. Consistent Error Responses

```java
private static void sendError(ServerResponse res, Status status, String msg, String details) {
    res.status(status).send(new ErrorResponse(msg, details));
}
```

- All errors use consistent JSON format
- Clients can parse programmatically

### Functional Routing Style (Why Helidon SE?)

- **Lambdas for handlers**: `(req, res) -> { ... }`
- **Explicit control flow** - no magic or reflection
- **Lightweight** - minimal framework overhead
- **vs MP** - MP uses annotations (more declarative, more abstraction)
- SE is better for understanding fundamentals

### HTTP Status Codes in Practice

- **200 OK** - GET/POST/PUT successful
- **201 Created** - POST returns new resource
- **204 No Content** - DELETE successful (empty body)
- **400 Bad Request** - Validation failed or invalid JSON
- **404 Not Found** - Resource doesn't exist

### Key Insight: JSON-B Auto-Binding

The magic:

```java
// Incoming JSON -> Java Object
User user = req.content().as(User.class);

// Java Object -> Outgoing JSON
res.send(user);
```

- Dependency: `helidon-http-media-jsonb`
- Transparent serialization/deserialization
- Works with standard POJOs (no annotations needed)

### Docker Multi-Stage Build Verified

- **Build stage**: Eclipse Temurin JDK (450MB)
  - Copies source and gradle wrapper
  - Runs `./gradlew :app:clean :app:build`
  - Creates fat JAR in /app/build/libs/
- **Runtime stage**: Eclipse Temurin JRE (180MB)
  - Copies only the fat JAR from build stage
  - Sets entrypoint with `-Dserver.port=8080`
  - Final image: ~200MB
  - Build time: ~30 seconds
- All endpoints respond correctly in container

### Testing Approach

1. **Local**: `./gradlew :app:run` (fastest feedback)
2. **Container**: `podman build` → `podman run` (production-like)
3. **End-to-end**: curl all CRUD operations + error cases
4. **Validation**: Inspect HTTP status codes and JSON responses

### What Worked Exceptionally Well

- ✅ SE's functional routing - lightweight and explicit
- ✅ JSON binding - transparent with zero boilerplate
- ✅ In-memory storage - perfect for learning, no DB setup
- ✅ ConcurrentHashMap - automatic thread safety
- ✅ Docker multi-stage - lean production images

### Upcoming Improvements (Next Sessions)

- 🔄 Integration tests - lock down CRUD behavior
- 🔄 Filters/Interceptors - request logging, timing
- 🔄 CORS configuration - cross-origin support
- 🔄 Database - H2 (dev) or PostgreSQL (prod)
- 🔄 Config management - application.yaml, profiles
- 🔄 Observability - metrics, health checks, tracing

### Git & Branching

- Branch: `phase-1.2-json-handling`
- Changes committed locally
- Next: Merge to main, tag v0.2

### Time & Effort

- Session 1 (Feb 18): Setup, troubleshooting, Dockerfile (2 hrs)
- Session 2 (Feb 19): JSON/CRUD, testing, validation (1.5 hrs)
- Total Phase 1.2: ~3.5 hours
