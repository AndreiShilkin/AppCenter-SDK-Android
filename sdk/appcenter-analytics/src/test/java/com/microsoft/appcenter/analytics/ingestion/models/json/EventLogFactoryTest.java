package com.microsoft.appcenter.analytics.ingestion.models.json;

import com.microsoft.appcenter.analytics.ingestion.models.EventLog;
import com.microsoft.appcenter.analytics.ingestion.models.one.json.CommonSchemaEventLogFactory;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import com.microsoft.appcenter.ingestion.models.one.PartAUtils;
import com.microsoft.appcenter.ingestion.models.one.PartCUtils;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.TypedProperty;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
public class EventLogFactoryTest {

    @Test
    public void createEvent() {
        EventLog eventLog = new EventLogFactory().create();
        assertNotNull(eventLog);
        assertEquals(EventLog.TYPE, eventLog.getType());
    }

    @Test
    public void dontConvertEventWithoutTargetTokens() {

        /* Create event log with just a name and no target. */
        EventLog log = new EventLog();
        log.setName("test");
        Collection<CommonSchemaLog> convertedLogs = new CommonSchemaEventLogFactory().toCommonSchemaLogs(log);
        assertNotNull(convertedLogs);
        assertEquals(0, convertedLogs.size());
    }

    @Test
    @PrepareForTest({PartAUtils.class, PartCUtils.class})
    public void convertEventWithoutProperties() {

        /* Mock utilities. */
        mockStatic(PartAUtils.class);
        mockStatic(PartCUtils.class);

        /* Create event log. */
        EventLog log = new EventLog();
        log.setName("test");

        /* Old properties are ignored. */
        Map<String, String> oldProperties = new HashMap<>();
        oldProperties.put("ignored", "ignored");
        log.setProperties(oldProperties);

        /* Set typed properties. */
        List<TypedProperty> properties = new ArrayList<>();
        StringTypedProperty stringTypedProperty = new StringTypedProperty();
        stringTypedProperty.setName("a");
        stringTypedProperty.setValue("b");
        properties.add(stringTypedProperty);
        log.setTypedProperties(properties);

        /* With 2 targets. */
        log.addTransmissionTarget("t1");
        log.addTransmissionTarget("t2");
        Collection<CommonSchemaLog> convertedLogs = new EventLogFactory().toCommonSchemaLogs(log);
        assertNotNull(convertedLogs);
        assertEquals(2, convertedLogs.size());

        /* Check name was added. */
        for (CommonSchemaLog commonSchemaLog : convertedLogs) {
            verifyStatic();
            PartAUtils.setName(same(commonSchemaLog), eq("test"));
        }

        /* Check Part A was added with target tokens. */
        verifyStatic();
        PartAUtils.addPartAFromLog(eq(log), notNull(CommonSchemaLog.class), eq("t1"));
        verifyStatic();
        PartAUtils.addPartAFromLog(eq(log), notNull(CommonSchemaLog.class), eq("t2"));

        /* Check Part C was added with typed properties (and thus not old ones). */
        verifyStatic(times(2));
        PartCUtils.addPartCFromLog(eq(properties), notNull(CommonSchemaLog.class));
    }
}
