# Quick Reference

## Current Status
- ✅ **Phase 1.1 Complete** - Basic Helidon SE setup
- 📍 **Branch**: `phase-1-basic-setup` 
- 🏷️ **Tag**: `v0.1`

## Git Workflow

### To Start Next Task (Phase 1.2)
```bash
# Create new branch from main
git checkout -b phase-1.2-json-handling

# Work on the task...
# (add JSON handling, write tests, etc.)

# When done, commit
git add .
git commit -m "Phase 1.2: Add JSON request/response handling"

# Merge to main
git checkout main
git merge phase-1.2-json-handling

# Tag if it's a milestone
git tag -a v0.2 -m "Phase 1.2: JSON handling complete"
```

### To Revisit Phase 1 Work
```bash
# Switch to the branch
git checkout phase-1-basic-setup

# Look around, test, experiment
./gradlew :app:run
curl http://localhost:8080/hello

# Go back to main when done
git checkout main
```

## Essential Commands

```bash
# See all branches
git branch -a

# See all tags
git tag -l -n

# Show commit history
git log --oneline --graph

# Compare branches
git diff main..phase-1-basic-setup
```

## Project Structure
```
weblogic-se-mp/
├── app/                    # Application code
│   ├── src/main/java/      # Source code
│   ├── src/test/java/      # Tests
│   ├── build.gradle        # App build config
│   └── Dockerfile          # Container definition
├── docs/                   # Documentation
│   ├── PLAN.md            # Learning roadmap
│   ├── PROGRESS.md        # Progress log
│   ├── GOALS.md           # Learning goals
│   ├── NOTES.md           # Technical notes
│   └── BRANCHING.md       # Git strategy
├── README.md              # Project overview
└── build.gradle           # Root build config
```

## Quick Test
```bash
# Run locally
./gradlew :app:run

# Run tests
./gradlew :app:test

# Build container
podman build -f app/Dockerfile -t helidon-se:1.0.0 .

# Run container
podman run --rm -p 8080:8080 helidon-se:1.0.0

# Test endpoints
curl http://localhost:8080/hello
curl http://localhost:8080/health
curl http://localhost:8080/echo/world
```

## Documentation
- [README.md](README.md) - Start here
- [docs/PLAN.md](docs/PLAN.md) - Full learning roadmap
- [docs/BRANCHING.md](docs/BRANCHING.md) - Git workflow details
- [docs/GOALS.md](docs/GOALS.md) - Current goals and progress
- [docs/NOTES.md](docs/NOTES.md) - Technical insights

## Next Steps
1. Review [docs/PLAN.md](docs/PLAN.md) for Phase 1.2 tasks
2. Create branch: `git checkout -b phase-1.2-json-handling`
3. Start with JSON request/response handling
4. Build simple CRUD API
