package io.slingr.endpoints.autotask.ws;

import io.slingr.endpoints.autotask.AutotaskEndpoint;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.utils.tests.EndpointTests;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.soap.SOAPException;
import java.util.Date;

import static org.junit.Assert.*;

@Ignore("For dev purposes only")
public class AutotaskEndpointTest {

    private static EndpointTests test;
    private static AutotaskEndpoint endpoint;

    @BeforeClass
    public static void init() throws Exception {
        test = EndpointTests.start(new io.slingr.endpoints.autotask.Runner(), "test.properties");
        endpoint = (AutotaskEndpoint) test.getEndpoint();
    }

    @Test
    public void testReadAccount() throws SOAPException {
        Json params = Json.map()
                .set("entity", "Account")
                .set("filters", Json.list()
                        .push(Json.map()
                                .set("field", "id")
                                .set("udf", false)
                                .set("op", "equals")
                                .set("value", 29760247l)
                        )
                );
        Json accounts = endpoint.query(params);
        Json account = (Json) accounts.object(0);
        assertNotNull(account);
        assertEquals(29760247l, (long) account.longInteger("id"));
        assertEquals("Test Account", account.string("AccountName"));
    }

    @Test
    public void testCreateAccount() {
        String accountName = "automated-test-account-7";

        // create account
        Json params = Json.map()
                .set("entity", "Account")
                .set("data", Json.map()
                        .set("AccountName", accountName)
                        .set("AccountNumber", "123")
                        .set("AccountType", 2)
                        .set("Active", true)
                        .set("Address1", "76 test")
                        .set("AssetValue", 77234.23)
                        .set("CountryID", 12)
                        .set("Phone", "867-5309")
                        .set("OwnerResourceID", 30759325)
                        .set("UserDefinedFields", Json.map()
                                .set("LFNOTE", "Test note!")
                        )
                );
        Json res = endpoint.create(params);
        assertNotNull(res);
        assertNotNull(res.longInteger("id"));
        long accountId = res.longInteger("id");
        System.out.println("account ID: "+accountId);

        // read and check data is OK
        params = Json.map()
                .set("entity", "Account")
                .set("filters", Json.list()
                        .push(Json.map()
                                .set("field", "id")
                                .set("op", "equals")
                                .set("value", accountId)
                        )
                );
        Json accounts = endpoint.query(params);
        Json account = (Json) accounts.object(0);
        assertNotNull(account);
        assertEquals(accountId, (long) account.longInteger("id"));
        assertEquals(accountName, account.string("AccountName"));
        assertEquals("123", account.string("AccountNumber"));
        assertEquals(2, (int) account.integer("AccountType"));
        assertEquals(true, account.bool("Active"));
        assertEquals("76 test", account.string("Address1"));
        assertEquals(77234.23, (double) account.decimal("AssetValue"), 0.1);
        assertEquals(12, (long) account.longInteger("CountryID"));
        assertEquals("Test note!", account.json("UserDefinedFields").string("LFNOTE"));
        assertTrue(account.object("CreateDate") instanceof Date);
    }

    @Test
    public void readCountries() {
        Json params = Json.map()
                .set("entity", "Country")
                .set("filters", Json.list()
                        .push(Json.map()
                                .set("field", "CountryCode")
                                .set("op", "IsNotNull")
                        )
                );
        Json countries = endpoint.query(params);
        for (Object obj : countries.objects()) {
            Json country = (Json) obj;
            System.out.println("Country: "+country.string("Name")+", "+country.string("CountryCode")+", "+country.longInteger("id"));
        }
    }

    @Test
    public void testDelete() {
        Json params = Json.map()
                .set("entity", "AccountToDo")
                .set("data", Json.map()
                        .set("id", 30072703l)
                );
        Json res = endpoint.delete(params);
        assertNotNull(res);
        assertNotNull(res.longInteger("id"));
    }

    @Test
    public void testGetEntityInfo() {
        Json params = Json.map()
                .set("entity", "Account");
        Json res = endpoint.getEntity(params);
        assertNotNull(res);
        assertEquals("Account", res.string("name"));
        System.out.println(res.toString());
    }

    @Test
    public void testGetEntityFieldsInfo() {
        Json params = Json.map()
                .set("entity", "Account");
        Json res = endpoint.getEntityFields(params);
        assertNotNull(res);
        assertTrue(res.size() > 0);
        System.out.println(res.toString());
    }
}
