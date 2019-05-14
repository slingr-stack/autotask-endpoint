endpoint.query = function(entityName, filters) {
    var params = {
        entity: entityName
    };
    if (filters) {
        params.filters = filters;
    }
    return endpoint._query(params);
};

endpoint.create = function(entityName, data) {
	var params = {
			entity: entityName,
			data: data
	}
	return endpoint._create(params).id;
}

endpoint.update = function(entityName, data) {
	var params = {
			entity: entityName,
			data: data
	};
	return endpoint._update(params).id;
};

endpoint.delete = function(entityName, id) {
    var params = {
        entity: entityName,
        data: {
        	id: id
		}
    };
    return endpoint._update(params).id;
};

endpoint.getEntity = function(entityName) {
    var params = {
        entity: entityName
    };
    return endpoint._getEntity(params);
};

endpoint.getEntityFields = function(entityName) {
    var params = {
        entity: entityName
    };
    return endpoint._getEntityFields(params);
};

endpoint.getEntityField = function(entityName, fieldName) {
    var fields = endpoint.getEntityFields(entityName);
    for (var i in fields) {
        var field = fields[i];
        if (field.name == fieldName) {
            return field;
        }
    }
};

endpoint.getWebUrl = function() {
    var res = endpoint._getWebUrl({});
    return res.webUrl;
};