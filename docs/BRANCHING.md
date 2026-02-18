# Git Branching Strategy

## Overview
Each learning phase/task gets its own branch for easy revisiting and reference.

## Branch Naming Convention
```
phase-{number}-{short-description}
```

## Main Branches

### `main`
- Stable, working code
- Documentation up to date
- All tests passing
- Production-ready state

### Task/Learning Branches
Each milestone or learning task gets a dedicated branch:

```
phase-1-basic-setup                 ✅ CURRENT
phase-1.2-json-handling            ⏳ Next
phase-1.2-crud-api                 ⏳
phase-1.3-configuration            ⏳
phase-1.4-observability            ⏳
phase-1.5-database-integration     ⏳
phase-2-gradle-deep-dive           ⏳
phase-3-helidon-mp                 ⏳
phase-4-weblogic-setup             ⏳
phase-5-migration                  ⏳
```

## Workflow

### Starting a New Task
```bash
# Update main branch
git checkout main
git pull

# Create new task branch
git checkout -b phase-X-description

# Work on the task...
```

### Completing a Task
```bash
# Commit your work
git add .
git commit -m "Complete phase X: description"

# Merge to main
git checkout main
git merge phase-X-description

# Keep branch for reference (don't delete)
git checkout phase-X-description
```

### Revisiting a Previous Task
```bash
# List all branches
git branch -a

# Checkout the branch
git checkout phase-1-basic-setup

# Review the code at that state
```

## Current Branches

### ✅ phase-1-basic-setup
**What's included:**
- Basic Helidon SE application
- Three REST endpoints: `/hello`, `/health`, `/echo/{msg}`
- Gradle build with fat JAR configuration
- Multi-stage Dockerfile
- Unit tests
- Complete documentation

**Key commits:**
- Initial project setup
- Fix fat JAR packaging
- Add documentation

**To revisit:**
```bash
git checkout phase-1-basic-setup
```

---

## Future Branches (Planned)

### phase-1.2-json-handling
**Goals:**
- Add JSON request/response support
- Jackson or JSON-B integration
- Handle POST requests with JSON body

### phase-1.2-crud-api
**Goals:**
- Create User or Product entity
- Implement CRUD operations
- In-memory storage first
- Proper error responses

### phase-1.3-configuration
**Goals:**
- Helidon Config API
- application.yaml files
- Environment-specific configs

### phase-1.4-observability
**Goals:**
- Metrics endpoint (Prometheus format)
- Enhanced health checks
- Structured logging
- Request tracing

### phase-1.5-database-integration
**Goals:**
- H2 database setup
- JDBC integration
- Connection pooling
- Database migrations

### phase-2-gradle-mastery
**Goals:**
- Custom Gradle tasks
- Build profiles
- Code coverage
- Integration test configuration

### phase-3-helidon-mp
**Goals:**
- Convert app to MicroProfile
- JAX-RS endpoints
- CDI usage
- Compare with SE version

### phase-4-weblogic-setup
**Goals:**
- WebLogic Server installation
- Traditional Java EE app
- Deployment understanding

### phase-5-migration-patterns
**Goals:**
- Migration strategies
- Hybrid deployment
- Performance comparison

---

## Best Practices

### DO:
✅ Create a branch before starting new work
✅ Keep branches for reference (don't delete)
✅ Merge completed work to main
✅ Tag important milestones
✅ Write descriptive commit messages

### DON'T:
❌ Work directly on main
❌ Delete learning branches
❌ Make commits without testing
❌ Mix unrelated changes in one branch

---

## Git Commands Reference

```bash
# List all branches
git branch -a

# Create and switch to new branch
git checkout -b branch-name

# Switch branches
git checkout branch-name

# Show current branch
git branch --show-current

# Commit with message
git commit -m "Description"

# Tag a milestone
git tag -a v1.0 -m "Milestone 1 complete"

# Show tags
git tag -l

# Compare branches
git diff main..phase-1-basic-setup

# Show branch history
git log --oneline --graph --all

# Stash changes
git stash
git stash pop
```

---

## Tagging Strategy

Major milestones get tags:

- `v0.1` - Phase 1.1 complete (basic setup) ✅
- `v0.2` - Phase 1.2 complete (CRUD API)
- `v0.3` - Phase 1.3-1.5 complete (full SE features)
- `v1.0` - Phase 3 complete (Helidon MP)
- `v2.0` - Phase 5 complete (WebLogic integration)

```bash
# Create annotated tag
git tag -a v0.1 -m "Phase 1.1: Basic Helidon SE setup complete"

# Push tags
git push origin v0.1

# List tags with details
git tag -l -n
```

---

**Last Updated**: Feb 18, 2026
**Current Branch**: phase-1-basic-setup
**Next Branch**: phase-1.2-json-handling
