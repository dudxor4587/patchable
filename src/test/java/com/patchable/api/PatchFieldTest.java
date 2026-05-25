package com.patchable.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PatchField 타입 테스트")
class PatchFieldTest {

    @Test
    @DisplayName("PatchField.unset() 은 Unset 인스턴스를 반환한다")
    void unset_factory() {
        PatchField<String> field = PatchField.unset();
        assertInstanceOf(PatchField.Unset.class, field);
    }

    @Test
    @DisplayName("PatchField.of(value) 는 Value 인스턴스를 반환한다")
    void of_factory() {
        PatchField<String> field = PatchField.of("hello");
        assertInstanceOf(PatchField.Value.class, field);
        assertEquals("hello", ((PatchField.Value<String>) field).value());
    }

    @Test
    @DisplayName("PatchField.delete() 는 Delete 인스턴스를 반환한다")
    void delete_factory() {
        PatchField<String> field = PatchField.delete();
        assertInstanceOf(PatchField.Delete.class, field);
    }

    @Test
    @DisplayName("sealed interface 는 Unset, Value, Delete 세 가지만 허용한다")
    void sealed_permits_only_three_types() {
        var permits = PatchField.class.getPermittedSubclasses();
        assertEquals(3, permits.length);
    }
}
