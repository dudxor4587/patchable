# Patchable

[한국어](README.md)

**A Java library that eliminates PATCH boilerplate while preserving domain methods.**

Auto-generates patchers like MapStruct, but calls domain methods instead of setters.

## The Problem

Writing PATCH APIs in Spring Boot means repeating this pattern for every entity:

```java
member.updateMember(
    request.name() != null ? request.name() : member.getName(),
    request.email() != null ? request.email() : member.getEmail(),
    // ... repeated for every field
);
```

Existing solutions each force a compromise:
- MapStruct / BeanUtils → **requires setters** (bypasses domain methods)
- JsonNullable → **wraps all DTO fields + boilerplate remains**
- JSON Patch → **clients must send operation arrays**

Patchable achieves **domain method invocation + 3-state support + zero boilerplate** simultaneously.

## Key Features

### 1. Domain Method Invocation

Generated patchers call your domain methods (e.g., `updateMember()`), not individual setters. Domain invariants — validation, business rules, state transitions — are preserved in PATCH operations.

### 2. PatchField — 3-State Representation

```java
public sealed interface PatchField<T> {
    record Unset<T>()        implements PatchField<T> {}  // not provided → skip
    record Value<T>(T value) implements PatchField<T> {}  // value provided → update
    record Delete<T>()       implements PatchField<T> {}  // explicit null → delete
}
```

Accurately maps JSON's three states:
- Key absent → `Unset` (skip)
- `"bio": "value"` → `Value` (update)
- `"bio": null` → `Delete` (delete)

### 3. Nullability-Driven Wrapping

```java
@PatchOf(Member.class)
public record MemberProfilePatch(
    String name,                          // required field — 2 states (skip / update)
    PatchField<String> bio                // optional field — 3 states (skip / update / delete)
) {}
```

Required (NOT NULL) fields use plain types. Optional (nullable) fields use `PatchField`. The wrapping decision mirrors your domain model's nullability.

## How It Works

1. Discovers DTOs annotated with `@PatchOf(Member.class)` at compile time
2. Matches DTO fields to entity's domain method parameters by name and type (signature inference)
3. Generates a Patcher class (`@Component`) that calls the matched domain method
4. Inject the generated Patcher via Spring DI

**Naming convention:**
- Generated Patcher class: `{DTO name}Patcher` (e.g., `MemberProfilePatch` → `MemberProfilePatchPatcher`)
- Method: `public void apply(Entity target, DTO source)`

**Constraints:**
- DTO field names must match domain method parameter names
- If names differ (e.g., API naming vs domain naming), map them in a presentation-layer converter and pass the resulting PatchDTO to the patcher
- The library does not perform type conversion — handle conversions in your converter before passing to the patcher

## Usage

### 1. Add Dependency

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.dudxor4587:patchable:v0.1.0'
    annotationProcessor 'com.github.dudxor4587:patchable:v0.1.0'
}
```

### 2. Define Patch DTO

```java
@PatchOf(Member.class)
public record MemberProfilePatch(
    String name,
    String email,
    PatchField<String> bio
) {}
```

### 3. Use Generated Patcher

```java
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberProfilePatchPatcher patcher;

    @Transactional
    public Member patch(Long id, MemberProfilePatch request) {
        Member member = repository.findById(id).orElseThrow();
        patcher.apply(member, request);
        return member;
    }
}
```

## Comparison

| Feature | MapStruct | JsonNullable | Patchable |
|---------|-----------|-------------|-----------|
| Invocation target | setters | domain methods (manual) | **domain methods (auto)** |
| Domain invariants | ❌ bypassed | user's responsibility | **✅ preserved** |
| 3-state support | ❌ | ✅ | **✅** |
| Requires setters on entity | yes | no | **no** |
| Boilerplate | none | remains | **none** |

## Requirements

- Java 17+
- Spring Boot 3.x+
- Jackson 2.x+
