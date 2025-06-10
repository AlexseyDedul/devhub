package com.addh.ws.auth_service.utils;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UtilityClass
public class UuidUtils {
    private static final TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator();

    // UUID v1 - time-based
    public static UUID timeBasedUuid(){
        return timeBasedGenerator.generate();
    }

    // UUID v5 - namespace + name based
    public static UUID fromName(String name){
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    // UUID v4 - fallback
    public static UUID randomUUID(){
        return UUID.randomUUID();
    }
}
