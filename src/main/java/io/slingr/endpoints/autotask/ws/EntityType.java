package io.slingr.endpoints.autotask.ws;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public enum EntityType {
    ACCOUNT("Account", Entity.class, "LastActivityDate", "CreateDate"),
    ACCOUNT_ALERT("AccountAlert", Entity.class, null, null),
    ACCOUNT_LOCATION("AccountLocation", Entity.class, null, null),
    ACCOUNT_NOTE("AccountNote", Entity.class, "LastModifiedDate", null),
    ACCOUNT_PHYSICAL_LOCATION("AccountPhysicalLocation", Entity.class, null, null),
    ACCOUNT_TEAM("AccountTeam", Entity.class, null, null),
    ACCOUNT_TO_DO("AccountToDo", Entity.class, "LastModifiedDate", "CreateDateTime"),
    ACTION_TYPE("ActionType", Entity.class, null, null),
    ADDITIONAL_INVOICE_FIELD_VALUE("AdditionalInvoiceFieldValue", Entity.class, null, null),
    ALLOCATION_CODE("AllocationCode", Entity.class, null, null),
    APPOINTMENT("Appointment", Entity.class, null, "CreateDateTime"),
    ATTACHMENT_INFO("AttachmentInfo", Entity.class, null, null),
    BILLING_ITEM("BillingItem", Entity.class, null, null),
    BILLING_ITEM_APPROVAL_LEVEL("BillingItemApprovalLevel", Entity.class, null, null),
    BUSINESS_DIVISION("BusinessDivision", Entity.class, null, null),
    BUSINESS_DIVISION_SUBDIVISION("BusinessDivisionSubdivision", Entity.class, null, null),
    BUSINESS_DIVISION_SUBDIVISION_RESOURCE("BusinessDivisionSubdivisionResource", Entity.class, null, null),
    BUSINESS_LOCATION("BusinessLocation", Entity.class, null, null),
    CHANGE_REQUEST_LINK("ChangeRequestLink", Entity.class, null, null),
    CLASSIFICATION_ICON("ClassificationIcon", Entity.class, null, null),
    CLIENT_PORTAL_USER("ClientPortalUser", Entity.class, null, null),
    CONTACT("Contact", Entity.class, "LastActivityDate", "CreateDate"),
    CONTRACT("Contract", Entity.class, null, null),
    CONTRACT_BLOCK("ContractBlock", Entity.class, null, null),
    CONTRACT_COST("ContractCost", Entity.class, null, "CreateDate"),
    CONTRACT_EXCLUSION_ALLOCATION_CODE("ContractExclusionAllocationCode", Entity.class, null, null),
    CONTRACT_EXCLUSION_ROLE("ContractExclusionRole", Entity.class, null, null),
    CONTRACT_FACTOR("ContractFactor", Entity.class, null, null),
    CONTRACT_MILESTONE("ContractMilestone", Entity.class, null, "CreateDate"),
    CONTRACT_NOTE("ContractNote", Entity.class, "LastActivityDate", null),
    CONTRACT_RATE("ContractRate", Entity.class, null, null),
    CONTRACT_RETAINER("ContractRetainer", Entity.class, null, null),
    CONTRACT_ROLE_COST("ContractRoleCost", Entity.class, null, null),
    CONTRACT_SERVICE("ContractService", Entity.class, null, null),
    CONTRACT_SERVICE_ADJUSTMENT("ContractServiceAdjustment", Entity.class, null, null),
    CONTRACT_SERVICE_BUNDLE("ContractServiceBundle", Entity.class, null, null),
    CONTRACT_SERVICE_BUNDLE_ADJUSTMENT("ContractServiceBundleAdjustment", Entity.class, null, null),
    CONTRACT_SERVICE_BUNDLE_UNIT("ContractServiceBundleUnit", Entity.class, null, null),
    CONTRACT_SERVICE_UNIT("ContractServiceUnit", Entity.class, null, null),
    CONTRACT_TICKET_PURCHASE("ContractTicketPurchase", Entity.class, null, null),
    COUNTRY("Country", Entity.class, null, null),
    CURRENCY("Currency", Entity.class, null, null),
    DEPARTMENT("Department", Entity.class, null, null),
    EXPENSE_ITEM("ExpenseItem", Entity.class, null, null),
    EXPENSE_REPORT("ExpenseReport", Entity.class, null, null),
    HOLIDAY("Holiday", Entity.class, null, null),
    HOLIDAY_SET("HolidaySet", Entity.class, null, null),
    INSTALLED_PRODUCT("InstalledProduct", Entity.class, null, "CreateDate"),
    INSTALLED_PRODUCT_TYPE("InstalledProductType", Entity.class, null, null),
    INSTALLED_PRODUCT_TYPE_UDF_ASSOCIATION("InstalledProductTypeUdfAssociation", Entity.class, null, null),
    INTERNAL_LOCATION("InternalLocation", Entity.class, null, null),
    INVENTORY_ITEM("InventoryItem", Entity.class, null, null),
    INVENTORY_ITEM_SERIAL_NUMBER("InventoryItemSerialNumber", Entity.class, null, null),
    INVENTORY_LOCATION("InventoryLocation", Entity.class, null, null),
    INVENTORY_TRANSFER("InventoryTransfer", Entity.class, null, null),
    INVOICE("Invoice", Entity.class, null, "CreateDateTime"),
    INVOICE_TEMPLATE("InvoiceTemplate", Entity.class, null, null),
    NOTIFICATION_HISTORY("NotificationHistory", Entity.class, null, null),
    OPPORTUNITY("Opportunity", Entity.class, "LastActivity", "CreateDate"),
    PAYMENT_TERM("PaymentTerm", Entity.class, null, null),
    PHASE("Phase", Entity.class, "LastActivityDateTime", "CreateDate"),
    PRICE_LIST_MATERIAL_CODE("PriceListMaterialCode", Entity.class, null, null),
    PRICE_LIST_PRODUCT("PriceListProduct", Entity.class, null, null),
    PRICE_LIST_ROLE("PriceListRole", Entity.class, null, null),
    PRICE_LIST_SERVICE("PriceListService", Entity.class, null, null),
    PRICE_LIST_SERVICE_BUNDLE("PriceListServiceBundle", Entity.class, null, null),
    PRICE_LIST_WORK_TYPE_MODIFIER("PriceListWorkTypeModifier", Entity.class, null, null),
    PRODUCT("Product", Entity.class, null, null),
    PRODUCT_VENDOR("ProductVendor", Entity.class, null, null),
    PROJECT("Project", Entity.class, null, "CreateDateTime"),
    PROJECT_COST("ProjectCost", Entity.class, null, "CreateDate"),
    PROJECT_NOTE("ProjectNote", Entity.class, "LastActivityDate", null),
    PURCHASE_ORDER("PurchaseOrder", Entity.class, null, "CreateDateTime"),
    PURCHASE_ORDER_ITEM("PurchaseOrderItem", Entity.class, null, null),
    PURCHASE_ORDER_RECEIVE("PurchaseOrderReceive", Entity.class, null, null),
    QUOTE("Quote", Entity.class, null, "CreateDate"),
    QUOTE_ITEM("QuoteItem", Entity.class, null, null),
    QUOTE_LOCATION("QuoteLocation", Entity.class, null, null),
    QUOTE_TEMPLATE("QuoteTemplate", Entity.class, null, "CreateDate"),
    RESOURCE("Resource", Entity.class, null, null),
    RESOURCE_ROLE("ResourceRole", Entity.class, null, null),
    RESOURCE_ROLE_DEPARTMENT("ResourceRoleDepartment", Entity.class, null, null),
    RESOURCE_ROLE_QUEUE("ResourceRoleQueue", Entity.class, null, null),
    RESOURCE_SKILL("ResourceSkill", Entity.class, null, null),
    ROLE("Role", Entity.class, null, null),
    SALES_ORDER("SalesOrder", Entity.class, null, null),
    SERVICE("Service", Entity.class, "LastModifiedDate", "CreateDate"),
    SERVICE_BUNDLE("ServiceBundle", Entity.class, "LastModifiedDate", "CreateDate"),
    SERVICE_BUNDLE_SERVICE("ServiceBundleService", Entity.class, null, null),
    SERVICE_CALL("ServiceCall", Entity.class, "LastModifiedDateTime", "CreateDateTime"),
    SERVICE_CALL_TASK("ServiceCallTask", Entity.class, null, null),
    SERVICE_CALL_TASK_RESOURCE("ServiceCallTaskResource", Entity.class, null, null),
    SERVICE_CALL_TICKET("ServiceCallTicket", Entity.class, null, null),
    SERVICE_CALL_TICKET_RESOURCE("ServiceCallTicketResource", Entity.class, null, null),
    SHIPPING_TYPE("ShippingType", Entity.class, null, null),
    SKILL("Skill", Entity.class, null, null),
    SUBSCRIPTION("Subscription", Entity.class, null, null),
    SUBSCRIPTION_PERIOD("SubscriptionPeriod", Entity.class, null, null),
    TASK("Task", Entity.class, "LastActivityDateTime", "CreateDateTime"),
    TASK_NOTE("TaskNote", Entity.class, "LastActivityDate", null),
    TASK_PREDECESSOR("TaskPredecessor", Entity.class, null, null),
    TASK_SECONDARY_RESOURCE("TaskSecondaryResource", Entity.class, null, null),
    TAX("Tax", Entity.class, null, null),
    TAX_CATEGORY("TaxCategory", Entity.class, null, null),
    TAX_REGION("TaxRegion", Entity.class, null, null),
    TICKET("Ticket", Entity.class, "LastActivityDate", "CreateDate"),
    TICKET_ADDITIONAL_CONTACT("TicketAdditionalContact", Entity.class, null, null),
    TICKET_CATEGORY("TicketCategory", Entity.class, null, null),
    TICKET_CATEGORY_FIELD_DEFAULTS("TicketCategoryFieldDefaults", Entity.class, null, null),
    TICKET_CHANGE_REQUEST_APPROVAL("TicketChangeRequestApproval", Entity.class, null, null),
    TICKET_CHECKLIST_ITEM("TicketChecklistItem", Entity.class, null, null),
    TICKET_COST("TicketCost", Entity.class, null, "CreateDate"),
    TICKET_NOTE("TicketNote", Entity.class, "LastActivityDate", null),
    TICKET_SECONDARY_RESOURCE("TicketSecondaryResource", Entity.class, null, null),
    TIME_ENTRY("TimeEntry", Entity.class, "LastModifiedDateTime", "CreateDateTime"),
    USER_DEFINED_FIELD_DEFINITION("UserDefinedFieldDefinition", Entity.class, null, null),
    USER_DEFINED_FIELD_LIST_ITEM("UserDefinedFieldListItem", Entity.class, null, null),
    WORK_TYPE_MODIFIER("WorkTypeModifier", Entity.class, null, null);

    private static final Logger logger = Logger.getLogger(EntityType.class);

    private String name;
    private Class<? extends Entity> clazz;
    private String modifiedField;
    private String createField;
    private EntityInfo info;
    private volatile boolean fieldsInitialized = false;
    private AutotaskApi autotaskApi = null;
    private Date lastPolling = new Date();
    private Long lastPollingId = null;

    EntityType(String name, Class<? extends Entity> clazz, String modifiedField, String createField) {
        this.name = name;
        this.clazz = clazz;
        this.modifiedField = modifiedField;
        this.createField = createField;
    }

    public String getName() {
        return name;
    }

    public String getModifiedField() {
        return modifiedField;
    }

    public String getCreateField() {
        return createField;
    }

    public Date getLastPolling() {
        return lastPolling;
    }

    public void setLastPolling(Date lastPolling) {
        this.lastPolling = lastPolling;
    }

    public Date getEffectiveLastPolling() {
        // create dates are truncated so we can't be precise so we go a day back to avoid missing a record
        // the polling algorithm should discard duplicates
        if (getModifiedField() == null && getCreateField() != null) {
            Date effectiveLastPolling = new Date(getLastPolling().getTime() - 1000*60*60*24);
            return effectiveLastPolling;
        } else {
            return getLastPolling();
        }
    }

    public Long getLastPollingId() {
        return lastPollingId;
    }

    public void setLastPollingId(Long lastPollingId) {
        this.lastPollingId = lastPollingId;
    }

    public synchronized EntityInfo getInfo() {
        if (!fieldsInitialized) {
            try {
                logger.info(String.format("Loading fields information for entity [%s]", name));
                List<EntityFieldInfo> allFields = new ArrayList<>();
                allFields.add(EntityFieldInfo.ID);
                allFields.addAll(autotaskApi.getFieldInfo(this));
                if (info.hasUserDefinedFields()) {
                    allFields.addAll(autotaskApi.getUDFInfo(this));
                }
                info.setFields(allFields);
                fieldsInitialized = true;
            } catch (SOAPException e) {
                logger.error(String.format("Error initializing fields for entity [%s]", this), e);
            } catch (AutotaskException e) {
                logger.error(String.format("Error initializing fields for entity [%s]", this), e);
            }
        }
        return info;
    }

    public void setInfo(EntityInfo info) {
        this.info = info;
    }

    public void setAutotaskApi(AutotaskApi autotaskApi) {
        this.autotaskApi = autotaskApi;
    }

    public Entity newInstance(Node xml) {
        try {
            return clazz.getConstructor(Node.class).newInstance(xml);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public Entity newInstance() {
    	try {
    		Entity entity = clazz.getConstructor(EntityType.class).newInstance(this);
    		entity.type = this;
    		return entity;
    	} catch (InstantiationException e) {
    		throw new IllegalStateException(e);
    	} catch(IllegalAccessException e) {
    		throw new IllegalStateException(e);
    	} catch(InvocationTargetException e) {
    		throw new IllegalStateException(e);
    	} catch(NoSuchMethodException e) {
    		throw new IllegalStateException(e);
    	}
    }

    static public EntityType getEntityTypeByName(String name) {
        for (EntityType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("[%s] is not a valid entity type", name));
    }
}
