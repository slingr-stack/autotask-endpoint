package io.slingr.endpoints.autotask.ws;

import io.slingr.endpoints.utils.Json;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntityFieldInfo {
    private EntityType entity;
    private String name;
    private String label;
    private EntityFieldType type;
    private String description;
    private boolean userDefinedField;
    private int length;
    private boolean required;
    private boolean readOnly;
    private boolean queryable;
    private boolean reference;
    private EntityType referenceEntityType;
    private boolean pickList;
    private List<PickValue> pickListValues;

    public static EntityFieldInfo ID = new EntityFieldInfo("id", "ID", EntityFieldType.LONG, true);

    private interface  FieldTypeConversions {
        Object fromXml(String value);
        String toXml(Object value);
        Object fromJson(Object value);
        Object toJson(Object value);
    }

    public enum EntityFieldType {
        SHORT("short", "xsd:short", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return Integer.valueOf(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        INTEGER("integer", "xsd:int", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return Integer.valueOf(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        LONG("long", "xsd:long", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return Long.valueOf(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        STRING("string", "xsd:string", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                return value;
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        DOUBLE("double", "xsd:double", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return Double.valueOf(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        FLOAT("float", "xsd:float", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return Float.valueOf(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        DATE_TIME("datetime", "xsd:dateTime", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return DateHelper.convertFromDateTime(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                if (value instanceof Date) {
                    return DateHelper.convertToDateTime((Date) value);
                } else if (value instanceof Long) {
                    Date date = new Date();
                    date.setTime((Long) value);
                    return DateHelper.convertToDateTime(date);
                } else if (value instanceof Integer) {
                    Date date = new Date();
                    date.setTime((Integer) value);
                    return DateHelper.convertToDateTime(date);
                } else if (value instanceof String) {
                    // in this case we trust the user sent a valid date in Autotask format
                    return value.toString();
                } else {
                    throw new IllegalArgumentException(String.format("Cannot convert value [%s] to date", value));
                }
            }

            @Override
            public Object fromJson(Object value) {
                if (value == null) {
                    return null;
                } else if (value instanceof Long) {
                    Date date = new Date((Long) value);
                    return date;
                } else if (value instanceof Integer) {
                    Date date = new Date((Integer) value);
                    return date;
                } else if (value instanceof String) {
                    try {
                        return DateHelper.convertFromUtcDateTime((String) value);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(String.format("Cannot convert value [%s] to date", value));
                    }
                } else {
                    throw new IllegalArgumentException(String.format("Cannot convert value [%s] to date", value));
                }
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        DATE("date", "xsd:string", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return value;
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        BOOLEAN("boolean", "xsd:boolean", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (value == null) {
                    return null;
                }
                if (value.equalsIgnoreCase("true") || value.equals("1")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return null;
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        }),
        DECIMAL("decimal", "xsd:decimal", new FieldTypeConversions() {
            @Override
            public Object fromXml(String value) {
                if (StringUtils.isBlank(value)) {
                    return null;
                }
                return new BigDecimal(value);
            }

            @Override
            public String toXml(Object value) {
                if (value == null) {
                    return "";
                }
                return value.toString();
            }

            @Override
            public Object fromJson(Object value) {
                return value;
            }

            @Override
            public Object toJson(Object value) {
                return value;
            }
        });

        private String code;
        private String xmlType;
        private FieldTypeConversions conversions;

        EntityFieldType(String code, String xmlType, FieldTypeConversions conversions) {
            this.code = code;
            this.xmlType = xmlType;
            this.conversions = conversions;
        }

        public String getCode() {
            return code;
        }

        public String getXmlType() {
            return xmlType;
        }

        public Object fromXml(String value) {
            return conversions.fromXml(value);
        }

        public String toXml(Object value) {
            return conversions.toXml(value);
        }

        public Object fromJson(Object value) {
            return conversions.fromJson(value);
        }

        public Object toJson(Object value) {
            return conversions.toJson(value);
        }

        public static EntityFieldType fromCode(String code) {
            for (EntityFieldType type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            return null;
        }
    }

    public class PickValue {
        private Object value;
        private String label;
        private boolean defaultValue;
        private int sortOrder;
        private boolean active;
        private boolean system;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public int getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isSystem() {
            return system;
        }

        public void setSystem(boolean system) {
            this.system = system;
        }

        public void fromXml(Node node) {
            value = type.fromXml(XmlHelper.getNodeValue("Value", node.getChildNodes()));
            label = XmlHelper.getNodeValue("Label", node.getChildNodes());
            defaultValue = Boolean.valueOf(XmlHelper.getNodeValue("IsDefaultValue", node.getChildNodes()));
            sortOrder = Integer.valueOf(XmlHelper.getNodeValue("SortOrder", node.getChildNodes()));
            active = Boolean.valueOf(XmlHelper.getNodeValue("IsActive", node.getChildNodes()));
            system = Boolean.valueOf(XmlHelper.getNodeValue("IsSystem", node.getChildNodes()));
        }
    }

    public EntityFieldInfo() {
    }

    public EntityFieldInfo(String name, String label, EntityFieldType type, boolean readOnly) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.readOnly = readOnly;
    }

    public EntityType getEntity() {
        return entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public EntityFieldType getType() {
        return type;
    }

    public void setType(EntityFieldType type) {
        this.type = type;
    }

    public boolean isUserDefinedField() {
        return userDefinedField;
    }

    public void setUserDefinedField(boolean userDefinedField) {
        this.userDefinedField = userDefinedField;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public EntityType getReferenceEntityType() {
        return referenceEntityType;
    }

    public void setReferenceEntityType(EntityType referenceEntityType) {
        this.referenceEntityType = referenceEntityType;
    }

    public boolean isPickList() {
        return pickList;
    }

    public void setPickList(boolean pickList) {
        this.pickList = pickList;
    }

    public List<PickValue> getPickListValues() {
        return pickListValues;
    }

    public void setPickListValues(List<PickValue> pickListValues) {
        this.pickListValues = pickListValues;
    }

    public void fromXml(Node node) {
        name = XmlHelper.getNodeValue("Name", node.getChildNodes());
        label = XmlHelper.getNodeValue("Label", node.getChildNodes());
        type = EntityFieldType.fromCode(XmlHelper.getNodeValue("Type", node.getChildNodes()));
        length = Integer.valueOf(XmlHelper.getNodeValue("Length", node.getChildNodes()));
        required = Boolean.valueOf(XmlHelper.getNodeValue("IsRequired", node.getChildNodes()));
        readOnly = Boolean.valueOf(XmlHelper.getNodeValue("IsReadOnly", node.getChildNodes()));
        queryable = Boolean.valueOf(XmlHelper.getNodeValue("IsQueryable", node.getChildNodes()));
        reference = Boolean.valueOf(XmlHelper.getNodeValue("IsReference", node.getChildNodes()));
        if (reference) {
            referenceEntityType = EntityType.getEntityTypeByName(XmlHelper.getNodeValue("ReferenceEntityType", node.getChildNodes()));
        }
        pickList = Boolean.valueOf(XmlHelper.getNodeValue("IsPickList", node.getChildNodes()));
        if (pickList) {
            pickListValues = new ArrayList<>();
            List<Node> valueElements = XmlHelper.getElementsByTagName(node, "PickListValue", false);
            for (Node valueElement : valueElements) {
                PickValue value = new PickValue();
                value.fromXml(valueElement);
                pickListValues.add(value);
            }
        }
    }

    public Json toJson() {
        Json json = Json.map()
                .set("name", name)
                .set("label", label)
                .set("type", type != null ? type.getCode() : null)
                .set("description", description)
                .set("userDefinedField", userDefinedField)
                .set("length", length)
                .set("required", required)
                .set("readOnly", readOnly)
                .set("queryable", queryable)
                .set("reference", reference)
                .set("referenceEntityType", referenceEntityType != null ? referenceEntityType.getName() : null)
                .set("pickList", pickList);
        if (pickList && pickListValues != null) {
            Json possibleValues = Json.list();
            for (PickValue value : pickListValues) {
                Json jsonValue = Json.map()
                        .set("value", type.toJson(value.getValue()))
                        .set("label", value.getLabel())
                        .set("defaultValue", value.isDefaultValue())
                        .set("sortOrder", value.getSortOrder())
                        .set("active", value.isActive())
                        .set("system", value.isSystem());
                possibleValues.push(jsonValue);
            }
            json.set("pickListValues", possibleValues);
        }
        return json;
    }
}
