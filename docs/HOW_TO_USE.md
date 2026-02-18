# How to Use This Learning Project

## 📖 Overview
This project is your hands-on learning lab for mastering Helidon, Gradle, WebLogic, and cloud-native development. Everything is organized to support incremental learning with git branches marking each milestone.

---

## 🎯 Your First Time Here

### 1. Read the Documentation
Start in this order:
1. **[../README.md](../README.md)** - Project overview and quick start
2. **[GOALS.md](GOALS.md)** - Understand your learning objectives
3. **[PLAN.md](PLAN.md)** - See the complete roadmap
4. **[BRANCHING.md](BRANCHING.md)** - Learn the git workflow

### 2. Verify Current Setup
```bash
# Check what branch you're on
git branch --show-current

# Run the application locally
./gradlew :app:run

# In another terminal, test it
curl http://localhost:8080/hello
curl http://localhost:8080/health

# Run tests
./gradlew :app:test

# Build and test container
podman build -f app/Dockerfile -t helidon-se:1.0.0 .
podman run --rm -p 8080:8080 helidon-se:1.0.0
```

### 3. Understand the Project Structure
```
weblogic-se-mp/
├── app/                        # Your application code lives here
│   ├── src/main/java/          # Java source files
│   │   └── com/acme/Main.java  # Main application (start here!)
│   ├── src/test/java/          # Test files
│   ├── build.gradle            # App-specific build config
│   └── Dockerfile              # Container image definition
│
├── docs/                       # All documentation (you are here!)
│   ├── HOW_TO_USE.md          # This file
│   ├── PLAN.md                # Complete learning roadmap
│   ├── PROGRESS.md            # What you've accomplished
│   ├── GOALS.md               # Current state and objectives
│   ├── NOTES.md               # Technical insights
│   └── BRANCHING.md           # Git workflow strategy
│
├── README.md                  # Project overview
├── QUICKREF.md                # Quick command reference
├── build.gradle               # Root build configuration
├── settings.gradle            # Multi-module setup
└── gradlew                    # Gradle wrapper (use this!)
```

---

## 🔄 Daily Workflow

### Starting Your Learning Session

```bash
# 1. Check current state
git branch --show-current
git status

# 2. Review what you're working on
cat docs/GOALS.md  # See current goals

# 3. Check the plan for today's focus
cat docs/PLAN.md   # See next tasks

# 4. If starting new phase, create branch
git checkout -b phase-X-description
```

### During Development

```bash
# Quick test cycle (fastest feedback)
./gradlew :app:run
# Test in browser or with curl
# Ctrl+C to stop

# Run tests frequently
./gradlew :app:test

# Check test reports if failures
open app/build/reports/tests/test/index.html

# Clean build when needed
./gradlew clean build
```

### Ending Your Session

```bash
# 1. Commit your work (even if incomplete)
git add .
git commit -m "WIP: Working on JSON handling"

# 2. Update PROGRESS.md with what you learned
vim docs/PROGRESS.md
# Add entry for today's date and learnings

# 3. If you completed a milestone, merge to main
git checkout main
git merge phase-X-description

# 4. Tag if it's a major milestone
git tag -a v0.X -m "Description"
```

---

## 🌿 Git Workflow for Learning

### The Branch Strategy

Each learning task gets its own branch so you can:
- ✅ Revisit previous states anytime
- ✅ Compare implementations
- ✅ Keep working code stable on `main`
- ✅ Experiment without fear

### Branch Naming
```
phase-{major}.{minor}-{short-description}

Examples:
- phase-1-basic-setup          (completed ✅)
- phase-1.2-json-handling      (next)
- phase-1.2-crud-api
- phase-1.3-configuration
```

### Creating a New Branch

```bash
# Always start from main
git checkout main

# Create and switch to new branch
git checkout -b phase-1.2-json-handling

# Now you're ready to work!
```

### Committing Work

```bash
# See what changed
git status
git diff

# Add files
git add .                    # Add everything
git add app/src/main/       # Or specific paths

# Commit with descriptive message
git commit -m "Add JSON serialization for User entity

- Added Jackson dependency
- Created User record class
- Updated endpoints to return JSON
- Added tests for JSON responses"
```

### Completing a Phase

```bash
# 1. Make sure everything works
./gradlew clean build
./gradlew :app:test

# 2. Update documentation
vim docs/PROGRESS.md         # Add what you learned
vim docs/GOALS.md           # Update progress tracking

# 3. Commit documentation updates
git add docs/
git commit -m "Update documentation for phase 1.2"

# 4. Merge to main
git checkout main
git merge phase-1.2-json-handling

# 5. Tag if major milestone
git tag -a v0.2 -m "Phase 1.2: JSON handling complete"

# 6. Keep branch for reference (don't delete!)
# Stay on main for next phase
```

### Revisiting Previous Work

```bash
# See all your learning milestones
git branch -a

# Checkout a previous phase
git checkout phase-1-basic-setup

# Explore the code
./gradlew :app:run
cat app/src/main/java/com/acme/Main.java

# When done, return to main
git checkout main
```

---

## 📝 Documentation Workflow

### What to Update and When

#### After Each Coding Session → PROGRESS.md
```markdown
## Session X: [Date]

### What I Worked On
- Implemented JSON request handling
- Added User entity
- Wrote tests for POST endpoint

### What I Learned
- Jackson vs JSON-B differences
- How Helidon handles content negotiation
- Record classes are perfect for DTOs

### Issues Encountered
- Had serialization error with timestamps
- Solved by adding @JsonFormat annotation

### Next Session
- Add PUT endpoint
- Implement validation
```

#### When Starting New Phase → GOALS.md
Update the progress percentage and current phase:
```markdown
### Current Phase: Phase 1.2 - JSON Handling
**Completion**: 30% ✅✅✅⚪⚪
```

#### When You Learn Something Important → NOTES.md
Add technical insights:
```markdown
## JSON Handling in Helidon SE

### Jackson Configuration
Found that Helidon SE requires manual Jackson setup...
[Your notes here]
```

#### When You Complete a Major Milestone → PLAN.md
Mark tasks as complete:
```markdown
### 1.2 JSON Handling ✅ (Completed)
- [x] Add JSON request/response handling
- [x] Create entity classes
```

---

## 🧪 Testing Workflow

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :app:test

# Run tests continuously (watch mode)
./gradlew test --continuous

# Run with more detail
./gradlew test --info

# Generate coverage report
./gradlew jacocoTestReport
open app/build/reports/jacoco/test/html/index.html
```

### Writing Tests

When you add new functionality:
1. ✅ Write test first (TDD approach) OR
2. ✅ Write test immediately after implementation
3. ✅ Run test to verify it passes
4. ✅ Commit code and test together

Test file location:
```
app/src/test/java/com/acme/YourTest.java
```

---

## 🚀 Container Workflow

### Building Containers

```bash
# Always build from project root
cd /Users/gaurav/projects/weblogic-se-mp

# Build image
podman build -f app/Dockerfile -t helidon-se:1.0.0 .

# Tag with version
podman tag helidon-se:1.0.0 helidon-se:latest
```

### Running Containers

```bash
# Run in foreground (see logs)
podman run --rm -p 8080:8080 helidon-se:1.0.0

# Run in background
podman run -d --name helidon-app -p 8080:8080 helidon-se:1.0.0

# Check logs
podman logs helidon-app
podman logs -f helidon-app  # Follow logs

# Stop container
podman stop helidon-app

# Remove container
podman rm helidon-app
```

### Container Development Workflow

```bash
# 1. Make code changes
vim app/src/main/java/com/acme/Main.java

# 2. Test locally first (faster)
./gradlew :app:run

# 3. Once working, build container
podman build -f app/Dockerfile -t helidon-se:dev .

# 4. Test in container
podman run --rm -p 8080:8080 helidon-se:dev
```

---

## 📊 Tracking Your Progress

### Weekly Review

Every week, do this:

```bash
# 1. Check git history
git log --oneline --since="1 week ago"

# 2. Review what you accomplished
cat docs/PROGRESS.md | tail -50

# 3. Update goals for next week
vim docs/GOALS.md

# 4. Plan next tasks
vim docs/PLAN.md
```

### Monthly Milestones

Every month:
1. ✅ Create a summary of major accomplishments
2. ✅ Tag a milestone version (v0.X)
3. ✅ Reflect on what worked well
4. ✅ Adjust learning pace if needed

---

## 🎓 Learning Best Practices

### The Learning Loop

```
1. Read documentation/plan
   ↓
2. Write code
   ↓
3. Test it
   ↓
4. Fix issues
   ↓
5. Document what you learned
   ↓
6. Commit to git
   ↓
7. Repeat!
```

### When You're Stuck

1. **Read the error message carefully**
   ```bash
   # Get full stack trace
   ./gradlew :app:run --stacktrace
   ```

2. **Check your notes first**
   ```bash
   grep -r "similar issue" docs/
   ```

3. **Search Helidon documentation**
   - https://helidon.io/docs/

4. **Experiment in isolation**
   - Create a minimal test case
   - Verify your assumptions

5. **Document the solution**
   - Add to NOTES.md
   - Help future you!

### Good Habits

✅ **DO:**
- Commit frequently (even WIP)
- Write tests as you go
- Document learnings immediately
- Take breaks when stuck
- Celebrate small wins

❌ **DON'T:**
- Skip documentation updates
- Delete branches (keep for reference)
- Work when too tired
- Copy code without understanding
- Rush through fundamentals

---

## 🔍 Quick Reference Commands

### Most Common Commands

```bash
# Development cycle
./gradlew :app:test && ./gradlew :app:run

# Git status
git status
git log --oneline
git branch -a

# Container workflow  
podman build -f app/Dockerfile -t helidon-se:1.0.0 .
podman run --rm -p 8080:8080 helidon-se:1.0.0

# Testing endpoints
curl http://localhost:8080/hello
curl http://localhost:8080/health
curl -X POST -H "Content-Type: application/json" \
  -d '{"name":"test"}' http://localhost:8080/api
```

### Useful Aliases (Add to ~/.bashrc or ~/.zshrc)

```bash
# Project aliases
alias cdw='cd /Users/gaurav/projects/weblogic-se-mp'
alias grun='./gradlew :app:run'
alias gtest='./gradlew :app:test'
alias gbuild='./gradlew clean build'

# Docker/Podman aliases
alias pbuild='podman build -f app/Dockerfile -t helidon-se:1.0.0 .'
alias prun='podman run --rm -p 8080:8080'
alias pstop='podman stop $(podman ps -q)'

# Git aliases
alias gs='git status'
alias gl='git log --oneline --graph'
alias gb='git branch -a'
```

---

## 📚 File Update Checklist

Use this checklist to keep documentation current:

### When You Start a New Phase
- [ ] Update `docs/GOALS.md` - Set current phase
- [ ] Create new git branch
- [ ] Review `docs/PLAN.md` for tasks

### During Development
- [ ] Update `docs/PROGRESS.md` - Add session notes
- [ ] Update `docs/NOTES.md` - Add technical insights
- [ ] Commit code frequently

### When You Complete a Phase
- [ ] Update `docs/PROGRESS.md` - Summarize completion
- [ ] Update `docs/GOALS.md` - Mark phase complete
- [ ] Update `docs/PLAN.md` - Check off tasks
- [ ] Update `README.md` - If features changed
- [ ] Merge branch to main
- [ ] Tag milestone

### Monthly
- [ ] Review all documentation for accuracy
- [ ] Archive old `PROGRESS.md` sections if needed
- [ ] Update learning estimates in `PLAN.md`
- [ ] Celebrate progress! 🎉

---

## 🆘 Troubleshooting

### Port Already in Use
```bash
lsof -i :8080
kill -9 <PID>
```

### Gradle Daemon Issues
```bash
./gradlew --stop
./gradlew clean
```

### Container Won't Build
```bash
# Check Docker context
pwd  # Should be project root

# Clean old images
podman system prune -a
```

### Git Conflicts
```bash
# See conflicts
git status

# Abort merge if needed
git merge --abort

# Or resolve manually, then:
git add .
git commit -m "Resolve merge conflicts"
```

### Tests Failing
```bash
# Run with more info
./gradlew test --info

# Check test report
open app/build/reports/tests/test/index.html

# Clean and retry
./gradlew clean test
```

---

## 🎯 Next Steps

### You're Ready to Code When:
- ✅ You've read this guide
- ✅ You understand the branch strategy
- ✅ You know how to run and test locally
- ✅ You know how to update documentation

### Start Here:
1. Check `docs/PLAN.md` for Phase 1.2 tasks
2. Create branch: `git checkout -b phase-1.2-json-handling`
3. Open `app/src/main/java/com/acme/Main.java`
4. Start coding!

### Remember:
- 📝 Document as you learn
- 🧪 Test frequently
- 💾 Commit often
- 🎯 Focus on understanding, not speed
- 🎉 Celebrate progress!

---

**Last Updated**: Feb 18, 2026  
**Current Phase**: Ready to start Phase 1.2  
**Your Progress**: Phase 1.1 Complete ✅

## 📌 IMPORTANT: Keep This File Updated!

As you progress through your learning:
- ✅ Add new workflows you discover
- ✅ Update commands that change
- ✅ Add troubleshooting tips you learn
- ✅ Document better practices you find
- ✅ Remove outdated information

**This is your living guide - make it yours!**

*Last reviewed: [Update this date when you review/update the file]*

