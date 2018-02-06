package ru.enke.sansara.login;

import org.jetbrains.annotations.Nullable;

public class LoginProperty {

    private final String name;
    private final String value;
    private final String signature;

    public LoginProperty(final String name, final String value, @Nullable final String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Nullable
    public String getSignature() {
        return signature;
    }

}
