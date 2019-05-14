package io.slingr.endpoints.autotask.ws;

import io.slingr.endpoints.utils.Json;
import org.w3c.dom.Node;

import java.util.List;

public class EntityInfo {
    private String entityName;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canQuery;
    private boolean canDelete;
    private boolean hasUserDefinedFields;
    private EntityInfoAccess userAccessForCreate;
    private EntityInfoAccess userAccessForUpdate;
    private EntityInfoAccess userAccessForQuery;
    private EntityInfoAccess userAccessForDelete;
    private List<EntityFieldInfo> fields;

    public enum EntityInfoAccess {
        NONE("None"), ALL("All"), RESTRICTED("Restricted"), INVALID("Invalid");

        private String code;

        EntityInfoAccess(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static EntityInfoAccess fromCode(String code) {
            for (EntityInfoAccess entityInfoAccess : values()) {
                if (entityInfoAccess.code.equals(code)) {
                    return entityInfoAccess;
                }
            }
            return null;
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public boolean canCreate() {
        return canCreate;
    }

    public void setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
    }

    public boolean canUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public boolean canQuery() {
        return canQuery;
    }

    public void setCanQuery(boolean canQuery) {
        this.canQuery = canQuery;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean hasUserDefinedFields() {
        return hasUserDefinedFields;
    }

    public void setHasUserDefinedFields(boolean hasUserDefinedFields) {
        this.hasUserDefinedFields = hasUserDefinedFields;
    }

    public EntityInfoAccess userAccessForCreate() {
        return userAccessForCreate;
    }

    public void setUserAccessForCreate(EntityInfoAccess userAccessForCreate) {
        this.userAccessForCreate = userAccessForCreate;
    }

    public EntityInfoAccess userAccessForUpdate() {
        return userAccessForUpdate;
    }

    public void setUserAccessForUpdate(EntityInfoAccess userAccessForUpdate) {
        this.userAccessForUpdate = userAccessForUpdate;
    }

    public EntityInfoAccess userAccessForQuery() {
        return userAccessForQuery;
    }

    public void setUserAccessForQuery(EntityInfoAccess userAccessForQuery) {
        this.userAccessForQuery = userAccessForQuery;
    }

    public EntityInfoAccess userAccessForDelete() {
        return userAccessForDelete;
    }

    public void setUserAccessForDelete(EntityInfoAccess userAccessForDelete) {
        this.userAccessForDelete = userAccessForDelete;
    }

    public List<EntityFieldInfo> getFields() {
        return fields;
    }

    public void setFields(List<EntityFieldInfo> fields) {
        this.fields = fields;
    }

    public EntityFieldInfo findField(String fieldName, boolean userDefinedField) {
        for (EntityFieldInfo field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName) && field.isUserDefinedField() == userDefinedField) {
                return field;
            }
        }
        return null;
    }

    public void fromXml(Node node) {
        String entityName = XmlHelper.getNodeValue("Name", node.getChildNodes());
        boolean canUpdate = Boolean.valueOf(XmlHelper.getNodeValue("CanUpdate", node.getChildNodes()));
        boolean canDelete = Boolean.valueOf(XmlHelper.getNodeValue("CanDelete", node.getChildNodes()));
        boolean canCreate = Boolean.valueOf(XmlHelper.getNodeValue("CanCreate", node.getChildNodes()));
        boolean canQuery = Boolean.valueOf(XmlHelper.getNodeValue("CanQuery", node.getChildNodes()));
        String userAccessForCreate = XmlHelper.getNodeValue("UserAccessForCreate", node.getChildNodes());
        String userAccessForQuery = XmlHelper.getNodeValue("UserAccessForQuery", node.getChildNodes());
        String userAccessForUpdate = XmlHelper.getNodeValue("UserAccessForUpdate", node.getChildNodes());
        String userAccessForDelete = XmlHelper.getNodeValue("UserAccessForDelete", node.getChildNodes());
        boolean hasUserDefinedFields = Boolean.valueOf(XmlHelper.getNodeValue("HasUserDefinedFields", node.getChildNodes()));
        this.setEntityName(entityName);
        this.setCanUpdate(canUpdate);
        this.setCanDelete(canDelete);
        this.setCanCreate(canCreate);
        this.setCanQuery(canQuery);
        this.setHasUserDefinedFields(hasUserDefinedFields);
        this.setUserAccessForCreate(EntityInfoAccess.fromCode(userAccessForCreate));
        this.setUserAccessForQuery(EntityInfoAccess.fromCode(userAccessForQuery));
        this.setUserAccessForUpdate(EntityInfoAccess.fromCode(userAccessForUpdate));
        this.setUserAccessForDelete(EntityInfoAccess.fromCode(userAccessForDelete));
    }

    public Json toJson() {
        Json json = Json.map()
                .set("name", entityName)
                .set("canCreate", canCreate)
                .set("canUpdate", canUpdate)
                .set("canQuery", canQuery)
                .set("canDelete", canDelete)
                .set("hasUserDefinedFields", hasUserDefinedFields)
                .set("userAccessForCreate", userAccessForCreate.getCode())
                .set("userAccessForUpdate", userAccessForUpdate.getCode())
                .set("userAccessForQuery", userAccessForQuery.getCode())
                .set("userAccessForDelete", userAccessForDelete.getCode());
        return json;
    }
}
