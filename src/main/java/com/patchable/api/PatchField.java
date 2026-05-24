package com.patchable.api;

public sealed interface PatchField<T> {

    record Unset<T>() implements PatchField<T> {}

    record Value<T>(T value) implements PatchField<T> {}

    record Delete<T>() implements PatchField<T> {}

    static <T> PatchField<T> unset() {
        return new Unset<>();
    }

    static <T> PatchField<T> of(T value) {
        return new Value<>(value);
    }

    static <T> PatchField<T> delete() {
        return new Delete<>();
    }
}
