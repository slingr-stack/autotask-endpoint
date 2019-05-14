package io.slingr.endpoints.autotask.ws;

public class EntityField {
	private EntityFieldInfo entityFieldInfo;
	private String originalName; // if entityFieldInfo is null, this will indicate the field name
	private boolean originalUdf; // if entityFieldInfo is null, this will indicate if this is a user field
	private String originalValue; // this is the XML value without any conversion
	private Object value;

	public EntityField(EntityFieldInfo entityFieldInfo) {
		this.entityFieldInfo = entityFieldInfo;
	}

	public EntityField(String originalName, boolean userField) {
		this.originalName = originalName;
		this.originalUdf = userField;
	}

	public EntityFieldInfo getEntityFieldInfo() {
		return entityFieldInfo;
	}

	public String getName() {
		if (entityFieldInfo != null) {
			return entityFieldInfo.getName();
		} else {
			return originalName;
		}
	}

	public EntityFieldInfo.EntityFieldType getType() {
		return entityFieldInfo.getType();
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public boolean getOriginalUdf() {
		return originalUdf;
	}

	public void setOriginalUdf(boolean originalUdf) {
		this.originalUdf = originalUdf;
	}

	public String getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setValueFromXml(String strValue) {
		if (entityFieldInfo != null && entityFieldInfo.getType() != null) {
			this.value = entityFieldInfo.getType().fromXml(strValue);
		} else {
			this.originalValue = strValue;
		}
	}

	public void setValueFromJson(Object value) {
		if (entityFieldInfo != null && entityFieldInfo.getType() != null) {
			this.value = entityFieldInfo.getType().fromJson(value);
		} else {
			if (value != null) {
				this.originalValue = value.toString();
			} else {
				this.originalValue = null;
			}
		}
	}

	public Object getValue() {
		if (value == null && originalValue != null) {
			return originalValue;
		} else {
			return value;
		}
	}

	public String getXmlValue() {
		if (entityFieldInfo == null || originalValue != null) {
			return originalValue;
		} else {
			return entityFieldInfo.getType().toXml(value);
		}
	}

	public Object getJsonValue() {
		if (entityFieldInfo == null || originalValue != null) {
			return originalValue;
		} else {
			return entityFieldInfo.getType().toJson(value);
		}
	}
}