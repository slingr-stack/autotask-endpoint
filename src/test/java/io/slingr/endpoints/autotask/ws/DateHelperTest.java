package io.slingr.endpoints.autotask.ws;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class DateHelperTest {
    @Test
    public void testUtcFormat() {
        String str = "2017-07-17T03:18:32.634Z";
        Date date = DateHelper.convertFromUtcDateTime(str);
        assertNotNull(date);
        System.out.println("Date: " + date);
    }
}
