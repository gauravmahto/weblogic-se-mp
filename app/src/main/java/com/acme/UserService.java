package com.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory storage for User entities.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // Add some sample data
        seedUser(new User("1", "Alice Johnson", "alice@example.com"));
        seedUser(new User("2", "Bob Smith", "bob@example.com"));
    }

    private void seedUser(User user) {
        users.put(user.getId(), user);
        trackId(user.getId());
    }

    private void trackId(String id) {
        if (id == null || id.isEmpty()) {
            return;
        }
        try {
            long numericId = Long.parseLong(id);
            idGenerator.updateAndGet(current -> Math.max(current, numericId + 1));
        } catch (NumberFormatException ignored) {
            // Ignore non-numeric IDs for the auto-increment sequence.
        }
    }

    /**
     * Create a new user, generating an ID if not provided.
     */
    public User createUser(User user) {
        String id = user.getId();
        if (id == null || id.isEmpty()) {
            id = String.valueOf(idGenerator.getAndIncrement());
        }
        User newUser = new User(id, user.getName(), user.getEmail());
        users.put(id, newUser);
        trackId(id);
        return newUser;
    }

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<User> findUsers(String name, String email) {
        String nameFilter = normalizeFilter(name);
        String emailFilter = normalizeFilter(email);
        List<User> results = new ArrayList<>();
        for (User user : users.values()) {
            if (matchesFilter(user.getName(), nameFilter)
                    && matchesFilter(user.getEmail(), emailFilter)) {
                results.add(user);
            }
        }
        return results;
    }

    private String normalizeFilter(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean matchesFilter(String value, String filter) {
        if (filter == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(filter);
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Update an existing user.
     */
    public Optional<User> updateUser(String id, User user) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        }
        User updated = new User(id, user.getName(), user.getEmail());
        users.put(id, updated);
        return Optional.of(updated);
    }

    /**
     * Delete a user.
     */
    public boolean deleteUser(String id) {
        return users.remove(id) != null;
    }
}
