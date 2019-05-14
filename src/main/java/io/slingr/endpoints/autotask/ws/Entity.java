package io.slingr.endpoints.autotask.ws;

import io.slingr.endpoints.utils.Json;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Entity {
    private static final Logger logger = Logger.getLogger(Entity.class);

    protected Map<String, EntityField> fields;
    protected Map<String, EntityField> userDefinedFields;
    protected EntityType type;

    public Entity(EntityType type) {
        fields = new HashMap<>();
        userDefinedFields = new HashMap<>();
        this.type = type;
    }

    public Entity(Node xml) {
        this((EntityType) null);
        fromXml(xml);
    }

    public Object getValue(String fieldName, boolean userDefinedField) {
        if (!userDefinedField && fields.containsKey(fieldName)) {
            return fields.get(fieldName).getValue();
        }
        if (userDefinedField && userDefinedFields.containsKey(fieldName)) {
            return userDefinedFields.get(fieldName).getValue();
        }
        return null;
    }

    public void setValue(String fieldName, boolean userDefinedField, Object value) {
        Map<String, EntityField> fieldMap;
        if (userDefinedField) {
            fieldMap = userDefinedFields;
        } else {
            fieldMap = fields;
        }
        if (fieldMap.containsKey(fieldName)) {
            fieldMap.get(fieldName).setValue(value);
        } else {
            EntityFieldInfo fieldInfo = type.getInfo().findField(fieldName, userDefinedField);
            if (fieldInfo == null) {
                throw new IllegalArgumentException(String.format("Field [%s] is not a valid field", fieldName));
            }
            EntityField entityField = new EntityField(fieldInfo);
            entityField.setValue(value);
            fieldMap.put(fieldName, entityField);
        }
    }


    public Long getId() {
        return (Long) getValue("id", false);
    }

    public Json toJson() {
        // this is a generic conversion; if you need more specific conversions, override this method for the entity class
        Json json = Json.map();
        Json userFieldsJson = Json.map();
        for (String key : fields.keySet()) {
            EntityField field = fields.get(key);
            json.set(key, field.getValue());
        }
        for (String key : userDefinedFields.keySet()) {
            EntityField field = userDefinedFields.get(key);
            userFieldsJson.set(key, field.getValue());
        }
        json.set("UserDefinedFields", userFieldsJson);
        return json;
    }

    public void fromJson(Json json) {
        if (json.contains("data") && !json.isEmpty("data")) {
            Json data = json.json("data");
            Iterator dataKeys = data.toMap().keySet().iterator();
            while (dataKeys.hasNext()) {
                String curKey = (String) dataKeys.next();
                if ("UserDefinedFields".equals(curKey)) {
                    Map userParamsMap = data.objectsMap("UserDefinedFields");
                    Iterator userKeys = userParamsMap.keySet().iterator();
                    while (userKeys.hasNext()) {
                        String curUserKey = (String) userKeys.next();
                        EntityFieldInfo fieldInfo = type.getInfo().findField(curUserKey, true);
                        EntityField field;
                        if (fieldInfo == null) {
                            logger.warn(String.format("Unknown user field [%s]. Value won't be converted.", curUserKey));
                            field = new EntityField(curUserKey, true);
                        } else {
                            field = new EntityField(fieldInfo);
                        }
                        field.setValueFromJson(userParamsMap.get(curUserKey));
                        userDefinedFields.put(curUserKey, field);
                    }
                } else {
                    EntityFieldInfo fieldInfo = type.getInfo().findField(curKey, false);
                    EntityField field;
                    if (fieldInfo == null) {
                        logger.error(String.format("Unknown field [%s]. Value won't be converted.", curKey));
                        field = new EntityField(curKey, false);
                    } else {
                        field = new EntityField(fieldInfo);
                    }
                    field.setValueFromJson(data.object(curKey));
                    fields.put(curKey, field);
                }
            }
        }
    }

    public void fromXml(Node xml) {
        if (xml.getAttributes().getNamedItem("xsi:type") != null) {
            type = EntityType.getEntityTypeByName(xml.getAttributes().getNamedItem("xsi:type").getNodeValue());
        }
        NodeList nodes = xml.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node curNode = nodes.item(i);
            if ("UserDefinedFields".equals(curNode.getNodeName())) {
                NodeList userFields = curNode.getChildNodes();
                for (int j = 0; j < userFields.getLength(); j++) {
                    Node userFieldNode = userFields.item(j);
                    String userFieldName = XmlHelper.getNodeValue("Name", userFieldNode.getChildNodes());
                    String userFieldValue = XmlHelper.getNodeValue("Value", userFieldNode.getChildNodes());
                    if (!StringUtils.isBlank(userFieldName)) {
                        EntityFieldInfo fieldInfo = type.getInfo().findField(userFieldName, true);
                        EntityField userField;
                        if (fieldInfo == null) {
                            logger.warn(String.format("Unknown user field [%s]. Value won't be converted.", userFieldName));
                            userField = new EntityField(userFieldName, true);
                        } else {
                            userField = new EntityField(fieldInfo);
                        }
                        try {
                            userField.setValueFromXml(userFieldValue);
                        } catch (Exception e) {
                            logger.error(String.format("There is a problem converting value of user field [%s] of type [%s]", fieldInfo.getName(), fieldInfo.getType()), e);
                            userField.setOriginalValue(userFieldValue);
                        }
                        userDefinedFields.put(userFieldName, userField);
                    }
                }
            } else {
                String fieldName = curNode.getNodeName();
                EntityFieldInfo fieldInfo = type.getInfo().findField(fieldName, false);
                EntityField field;
                if (fieldInfo == null) {
                    logger.error(String.format("Unknown field [%s]. Value won't be converted.", fieldName));
                    field = new EntityField(fieldName, false);
                } else {
                    field = new EntityField(fieldInfo);
                }
                try {
                    field.setValueFromXml(XmlHelper.getNodeValue(curNode));
                } catch (Exception e) {
                    logger.error(String.format("There is a problem converting value of field [%s] of type [%s]", fieldInfo.getName(), fieldInfo.getType()), e);
                    field.setOriginalValue(XmlHelper.getNodeValue(curNode));
                }
                fields.put(field.getName(), field);
            }
        }
    }

    public void toXml(SOAPElement entities) throws SOAPException {
        SOAPElement entity = entities.addChildElement("Entity", "atns");
        entity.setAttribute("xsi:type", "atns:" + type.getName());
        Iterator<String> fields = this.fields.keySet().iterator();
        while (fields.hasNext()) {
            String fieldName = fields.next();
            EntityField curField = this.fields.get(fieldName);
            SOAPElement fieldElement = entity.addChildElement(fieldName, "atns");
            if (curField.getEntityFieldInfo() != null && curField.getType() != null && curField.getType().getXmlType() != null && !fieldName.equals("id")) {
                fieldElement.setAttribute("xsi:type", curField.getType().getXmlType());
            }
            fieldElement.addTextNode(curField.getXmlValue());
        }
        if (!userDefinedFields.isEmpty()) {
            SOAPElement udfs = entity.addChildElement("UserDefinedFields", "atns");
            Iterator<String> userFields = userDefinedFields.keySet().iterator();
            while (userFields.hasNext()) {
                EntityField userField = userDefinedFields.get(userFields.next());
                SOAPElement udf = udfs.addChildElement("UserDefinedField", "atns");
                SOAPElement name = udf.addChildElement("Name", "atns");
                name.addTextNode(userField.getName());
                SOAPElement value = udf.addChildElement("Value", "atns");
                value.addTextNode(userField.getXmlValue());
            }
        }
    }

    private Integer getIntegerValue(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            return Integer.valueOf(str);
        }
    }

    private Long getLongValue(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            return Long.valueOf(str);
        }
    }

    private Double getDoubleValue(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            return Double.valueOf(str);
        }
    }

    private Boolean getBooleanValue(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            return Boolean.valueOf(str);
        }
    }

    private Date getDateValue(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            return DateHelper.convertFromDateTime(str);
        }
    }
}
