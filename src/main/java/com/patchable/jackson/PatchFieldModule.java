package com.patchable.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.patchable.api.PatchField;

public class PatchFieldModule extends SimpleModule {

    public PatchFieldModule() {
        super("PatchFieldModule");
        addDeserializer(PatchField.class, new PatchFieldDeserializer());
    }
}
