package org.mjkrumlauf.lightbulb;

import java.util.UUID;

public class UsageLimitExceeded extends Error {
    public UsageLimitExceeded(UUID bulbId) {
        super("Light bulb " + bulbId + " usage limit exceeded");
    }
}