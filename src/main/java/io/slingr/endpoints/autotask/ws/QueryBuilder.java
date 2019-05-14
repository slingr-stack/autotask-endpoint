package io.slingr.endpoints.autotask.ws;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    private EntityType entityType;
    private List<Filter> filters = new ArrayList<>();

    public class Filter {
        private EntityFieldInfo field;
        private String operation;
        private String value;

        public Filter(EntityFieldInfo field, String operation, String value) {
            this.field = field;
            this.operation = operation;
            this.value = value;
        }

        public EntityFieldInfo getField() {
            return field;
        }

        public void setField(EntityFieldInfo field) {
            this.field = field;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public QueryBuilder(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void addFilter(String field, Boolean udf, String operation, String value) {
        EntityFieldInfo fieldInfo;
        if (udf == null || !udf) {
            fieldInfo = entityType.getInfo().findField(field, false);
        } else {
            fieldInfo = entityType.getInfo().findField(field, true);
        }
        if (fieldInfo == null) {
            throw new IllegalArgumentException(String.format("Query field [%s] is not valid", field));
        }
        filters.add(new Filter(fieldInfo, operation, value));
    }

    public String getXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<queryxml>");
        sb.append("<entity>").append(entityType.getName()).append("</entity>");
        if (!filters.isEmpty()) {
            sb.append("<query>");
            filters.stream().forEach(filter -> {
                sb.append("<field>")
                        .append(filter.getField().getName())
                        .append("<expression op=\"")
                        .append(filter.getOperation())
                        .append("\"")
                        .append(filter.getField().isUserDefinedField() ? "udf=\"true\"" : "")
                        .append(">")
                        .append(filter.getField().getType().toXml(filter.getValue()))
                        .append("</expression></field>");
            });
            sb.append("</query>");
        }
        sb.append("</queryxml>");
        return sb.toString();
    }
}
