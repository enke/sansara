package ru.enke.sansara.login;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class LoginProfile {

    private final UUID uuid;
    private final String name;
    private final Map<String, LoginProperty> properties;

    public LoginProfile(UUID uuid, String name) {
        this(uuid, name, Collections.emptyMap());
    }

    public LoginProfile(final UUID uuid, final String name, final Map<String, LoginProperty> properties) {
        this.uuid = uuid;
        this.name = name;
        this.properties = properties;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Map<String, LoginProperty> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

}
