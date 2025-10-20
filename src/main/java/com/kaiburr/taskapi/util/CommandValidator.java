package com.kaiburr.taskapi.util;

import java.util.regex.Pattern;

public class CommandValidator {

    private static final Pattern SAFE_COMMAND_PATTERN = Pattern.compile(
            "^(echo|ls|pwd|whoami|date|uptime|cat|touch|mkdir|sleep|head|tail|df|du)(\\s+[^;&|`$><]*)?$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern BLOCK_OPERATORS = Pattern.compile("[;&|`$><]");

    public static void validate(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        String cmd = command.trim();

        if (!SAFE_COMMAND_PATTERN.matcher(cmd).matches()) {
            throw new IllegalArgumentException("Command not allowed: " + cmd);
        }

        if (BLOCK_OPERATORS.matcher(cmd).find()) {
            throw new IllegalArgumentException("Unsafe operators detected in command: " + cmd);
        }
    }
}
