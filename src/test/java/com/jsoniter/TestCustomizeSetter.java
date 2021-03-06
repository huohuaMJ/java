package com.jsoniter;

import com.jsoniter.spi.*;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestCustomizeSetter extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class ObjectWithDefaultSetter {
        String field;

        public void setField(String field) {
            this.field = field;
        }
    }

    public void test_default_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field': 'hello'}".replace('\'', '"'));
        ObjectWithDefaultSetter obj = iter.read(ObjectWithDefaultSetter.class);
        assertEquals("hello", obj.field);
    }

    public static class ObjectWithCustomizedSetter {
        String field1;
        String field2;

        public void initialize(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    public void test_customized_setter() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public void updateClassDescriptor(final ClassDescriptor desc) {
                if (desc.clazz == ObjectWithCustomizedSetter.class) {
                    desc.fields.clear();
                    desc.setters = (List) Arrays.asList(new SetterDescriptor(){{
                        try {
                            method = desc.clazz.getMethod("initialize", String.class, String.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                        methodName = "initialize";
                        parameters = (List) Arrays.asList(new Binding(desc.clazz, desc.lookup, String.class) {{
                            name = "field1";
                        }}, new Binding(desc.clazz, desc.lookup, String.class) {{
                            name = "field2";
                        }});
                    }});
                }
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': 'hello', 'field2': 'world'}".replace('\'', '"'));
        ObjectWithCustomizedSetter obj = iter.read(ObjectWithCustomizedSetter.class);
        assertEquals("hello", obj.field1);
        assertEquals("world", obj.field2);
    }
}
