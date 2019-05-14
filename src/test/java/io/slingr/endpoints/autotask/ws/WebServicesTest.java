package io.slingr.endpoints.autotask.ws;

import io.slingr.endpoints.utils.Json;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.soap.SOAPException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Ignore("For dev purposes only")
public class WebServicesTest {
    private static final String USERNAME = "youruser";
    private static final String PASSWORD = "password";
    private static final String INTEGRATION_CODE = "autotask-integration-code";

    @Test
    public void getZoneInfo() throws SOAPException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        String zoneInfo = autotaskApi.getZoneUrl();
        assertEquals("https://webservices15.autotask.net/ATServices/1.5/atws.asmx", zoneInfo);
    }

    @Test
    public void getWebUrl() throws SOAPException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        String webUrl= autotaskApi.getWebUrl();
        assertEquals("https://ww15.autotask.net/", webUrl);
    }

    @Test
    public void getEntityInfo() throws SOAPException, AutotaskException {
        AutotaskApi autotaskApi = new AutotaskApi("kevin@disrupt-it.com", PASSWORD, INTEGRATION_CODE);
        List<EntityInfo> entityInfoList = autotaskApi.getEntityInfo();
        assertTrue(entityInfoList.size() > 0);
    }

    @Test
    public void getFieldInfo() throws AutotaskException, SOAPException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        List<EntityFieldInfo> entityFieldInfoList = autotaskApi.getFieldInfo(EntityType.ACCOUNT);
        assertTrue(entityFieldInfoList.size() > 0);
    }

    @Test
    public void getUDFInfo() throws AutotaskException, SOAPException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        List<EntityFieldInfo> entityFieldInfoList = autotaskApi.getUDFInfo(EntityType.ACCOUNT);
        assertTrue(entityFieldInfoList.size() > 0);
    }

    @Test
    public void listAccounts() throws RemoteException, SOAPException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        QueryBuilder queryBuilder = new QueryBuilder(EntityType.ACCOUNT);
        queryBuilder.addFilter("id", false, "equals", "29760247");
        List<Entity> accounts = (List<Entity>) autotaskApi.query(queryBuilder);
        assertEquals(1, accounts.size());
        assertEquals("Test Account", accounts.get(0).getValue("AccountName", false));
    }

    @Test
    public void entityToJson() throws RemoteException, SOAPException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        QueryBuilder queryBuilder = new QueryBuilder(EntityType.ACCOUNT);
        queryBuilder.addFilter("id", false, "equals", "29760247");
        List<Entity> accounts = (List<Entity>) autotaskApi.query(queryBuilder);
        assertEquals(1, accounts.size());
        Json json = accounts.get(0).toJson();
        assertEquals(Long.valueOf(29760247), json.longInteger("id"));
        assertEquals("Test Account", json.string("AccountName"));
        assertEquals("Question", json.json("UserDefinedFields").string("ATOppPromotion"));
        assertTrue(json.object("ClientPortalActive") instanceof Boolean);
        assertTrue(json.object("LastActivityDate") instanceof Date);
    }

    @Test
    public void createTicket() throws SOAPException, AutotaskException {
        AutotaskApi autotaskApi = new AutotaskApi(USERNAME, PASSWORD, INTEGRATION_CODE);
        QueryBuilder queryBuilder = new QueryBuilder(EntityType.ACCOUNT);
        queryBuilder.addFilter("id", false, "equals", "30053684");
        List<Entity> accounts = (List<Entity>) autotaskApi.query(queryBuilder);
        assertEquals(1, accounts.size());
        Entity ticket = new Entity(EntityType.TICKET);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 60 * 60 * 24);
        ticket.setValue("AccountID", false, ""+accounts.get(0).getId());
        ticket.setValue("DueDateTime", false, date);
        ticket.setValue("Priority", false, "2");
        ticket.setValue("Status", false, "1");
        ticket.setValue("Title", false, "test ticket A");
        ticket.setValue("Description", false, "this is a test ticket");
        ticket.setValue("AssignedResourceID", false, "31053307");
        ticket.setValue("AssignedResourceRoleID", false, "29720942");
        Long ticketId = autotaskApi.create(ticket);
        assertNotNull(ticketId);
        System.out.println("ticket id: " + ticketId);
    }
}
