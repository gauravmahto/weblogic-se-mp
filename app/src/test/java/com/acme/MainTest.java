package com.acme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void mainClassLoads() {
        assertDoesNotThrow(() -> Class.forName("com.acme.Main"));
    }
}
