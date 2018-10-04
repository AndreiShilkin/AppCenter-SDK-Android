package com.microsoft.appcenter.ingestion.models.properties;

import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TypedPropertyUtilsTest {

    @Test
    public void createValidTypedProperty() throws JSONException {
        assertTrue(TypedPropertyUtils.create(BooleanTypedProperty.TYPE) instanceof BooleanTypedProperty);
        assertTrue(TypedPropertyUtils.create(DateTimeTypedProperty.TYPE) instanceof DateTimeTypedProperty);
        assertTrue(TypedPropertyUtils.create(DoubleTypedProperty.TYPE) instanceof DoubleTypedProperty);
        assertTrue(TypedPropertyUtils.create(LongTypedProperty.TYPE) instanceof LongTypedProperty);
        assertTrue(TypedPropertyUtils.create(StringTypedProperty.TYPE) instanceof StringTypedProperty);
    }

    @Test(expected = JSONException.class)
    public void createInvalidTypedProperty() throws JSONException {
        TypedPropertyUtils.create("Something");
    }
}
