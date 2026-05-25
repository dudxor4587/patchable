package com.patchable.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patchable.api.PatchField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PatchField Jackson Deserializer 테스트")
class PatchFieldDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new PatchFieldModule());
    }

    record TestDto(
            String name,
            PatchField<String> bio
    ) {}

    @Test
    @DisplayName("JSON 에 키가 없으면 PatchField.Unset 으로 역직렬화된다")
    void missing_field_should_be_Unset() throws Exception {
        var json = """
                { "name": "test" }
                """;

        var dto = mapper.readValue(json, TestDto.class);

        assertEquals("test", dto.name());
        assertInstanceOf(PatchField.Unset.class, dto.bio());
    }

    @Test
    @DisplayName("JSON 에 null 로 명시하면 PatchField.Delete 로 역직렬화된다")
    void null_field_should_be_Delete() throws Exception {
        var json = """
                { "name": "test", "bio": null }
                """;

        var dto = mapper.readValue(json, TestDto.class);

        assertEquals("test", dto.name());
        assertInstanceOf(PatchField.Delete.class, dto.bio());
    }

    @Test
    @DisplayName("JSON 에 값이 있으면 PatchField.Value 로 역직렬화된다")
    void present_field_should_be_Value() throws Exception {
        var json = """
                { "name": "test", "bio": "hello" }
                """;

        var dto = mapper.readValue(json, TestDto.class);

        assertEquals("test", dto.name());
        assertInstanceOf(PatchField.Value.class, dto.bio());
        assertEquals("hello", ((PatchField.Value<String>) dto.bio()).value());
    }

    @Test
    @DisplayName("일반 String 필드가 null 이면 Java null 이 된다")
    void plain_null_field_should_be_null() throws Exception {
        var json = """
                { "name": null }
                """;

        var dto = mapper.readValue(json, TestDto.class);

        assertNull(dto.name());
        assertInstanceOf(PatchField.Unset.class, dto.bio());
    }

    @Test
    @DisplayName("모든 필드가 있으면 각각 올바른 타입으로 역직렬화된다")
    void all_fields_present() throws Exception {
        var json = """
                { "name": "test", "bio": "world" }
                """;

        var dto = mapper.readValue(json, TestDto.class);

        assertEquals("test", dto.name());
        assertInstanceOf(PatchField.Value.class, dto.bio());
    }

    @Test
    @DisplayName("빈 JSON 이면 String 은 null, PatchField 는 Unset 이 된다")
    void empty_json_should_have_all_defaults() throws Exception {
        var json = "{}";

        var dto = mapper.readValue(json, TestDto.class);

        assertNull(dto.name());
        assertInstanceOf(PatchField.Unset.class, dto.bio());
    }
}
