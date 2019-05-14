package io.slingr.endpoints.autotask;

import io.slingr.endpoints.Endpoint;
import io.slingr.endpoints.autotask.polling.PollingService;
import io.slingr.endpoints.autotask.ws.*;
import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.exceptions.ErrorCode;
import io.slingr.endpoints.framework.annotations.ApplicationLogger;
import io.slingr.endpoints.framework.annotations.EndpointFunction;
import io.slingr.endpoints.framework.annotations.EndpointProperty;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.ws.exchange.FunctionRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.List;

@SlingrEndpoint(name = "autotask")
public class AutotaskEndpoint extends Endpoint {
    private static final Logger logger = Logger.getLogger(AutotaskEndpoint.class);

    @ApplicationLogger
    private AppLogs appLogger;

    @EndpointProperty
    private String username;

    @EndpointProperty
    private String password;

    @EndpointProperty
    private String integrationCode;

    @EndpointProperty
    private String pollingEnabled;

    @EndpointProperty
    private String pollingFrequency;

    @EndpointProperty
    private String entitiesToPoll;

    private AutotaskApi autotaskApi;

    private PollingService pollingService;

    public AutotaskEndpoint() {
    }

    public AutotaskEndpoint(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void endpointStarted() {
        try {
        	logger.info("Checking connection with Autotask...");
            autotaskApi = new AutotaskApi(username, password, integrationCode);
            logger.info("Connection is OK");
            logger.info("Loading entities information");
			List<EntityInfo> entityInfoList = autotaskApi.getEntityInfo();
			for (EntityInfo entityInfo : entityInfoList) {
			    try {
                    EntityType entityType = EntityType.getEntityTypeByName(entityInfo.getEntityName());
                    if (entityType == null) {
                        logger.error(String.format("Entity type [%s] is not supported by the endpoint", entityInfo.getEntityName()));
                        continue;
                    }
                    entityType.setAutotaskApi(autotaskApi);
                    entityType.setInfo(entityInfo);
                } catch (IllegalArgumentException iae) {
			        logger.error(String.format("There is a problem load entity info for [%s]. Probably the endpoint is out of date.", entityInfo.getEntityName()), iae);
                }
			}
			logger.info(String.format("Polling is [%s]", pollingEnabled));
			if ("enable".equals(pollingEnabled)) {
			    logger.info(String.format("Polling frequency is [%s] minutes", pollingFrequency));
                logger.info(String.format("Entities to poll are [%s]", entitiesToPoll));
                List<EntityType> entityTypesToPoll = new ArrayList<>();
                for (String entityToPoll : StringUtils.split(entitiesToPoll, ",")) {
                    EntityType entityType = EntityType.getEntityTypeByName(entityToPoll.trim());
                    entityTypesToPoll.add(entityType);
                }
                pollingService = new PollingService(Integer.valueOf(pollingFrequency), entityTypesToPoll,
                        (eventName, data) -> events().send(eventName, data), autotaskApi);
                pollingService.run();
            }
		} catch (SOAPException e) {
            appLogger.error("There was a problem configuring the Autotask API. Please check credentials.", e);
        } catch (AutotaskException ate) {
        	appLogger.error("Error fetching information of entities and fields", ate);
		}
    }

    @EndpointFunction(name = "_query")
    public Json query(Json params) {
        logger.info("Calling _query, params: " + params.toString());
        EntityType entityType = EntityType.getEntityTypeByName(params.string("entity"));
        QueryBuilder queryBuilder = new QueryBuilder(entityType);
        try {
            if (params.contains("filters") && !params.isEmpty("filters")) {
                for (Json filter : params.jsons("filters")) {
                    queryBuilder.addFilter(filter.string("field"), filter.bool("udf"), filter.string("op"), filter.string("value"));
                }
            }
        } catch (Exception e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, String.format("Error parsing query [%s]", params.toString()), e);
        }
        List<Entity> entities;
        try {
            entities = (List<Entity>) autotaskApi.query(queryBuilder);
        } catch (SOAPException e) {
            throw EndpointException.permanent(ErrorCode.API, String.format("Error executing query [%s]", queryBuilder.getXML()), e);
        }
        Json result = Json.list();
        entities.stream().forEach(entity -> result.push(entity.toJson()));
        return result;
    }
    
    @EndpointFunction(name = "_create")
    public Json create(Json params) {
        logger.info("Calling _create, params: " + params.toString());
    	Long createdId = null;
        EntityType entityType;
        Entity instance;
    	try {
            entityType = EntityType.getEntityTypeByName(params.string("entity"));
            instance = entityType.newInstance();
            instance.fromJson(params);
        } catch (IllegalArgumentException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error parsing the entity", e);
        }
        try {
            createdId = autotaskApi.create(instance);
        } catch (SOAPException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error creating the entity", e);
        } catch (AutotaskException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error creating the entity", e);
        }
    	Json result = Json.map().set("id", createdId);
    	return result;
    }
    
    @EndpointFunction(name = "_update")
    public Json update(Json params) {
        logger.info("Calling _update, params: " + params.toString());
    	Long updatedId = null;
        EntityType entityType;
        Entity instance;
    	try {
            entityType = EntityType.getEntityTypeByName(params.string("entity"));
            instance = entityType.newInstance();
            instance.fromJson(params);
        } catch (IllegalArgumentException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error parsing the entity", e);
        }
    	try {
	    	updatedId = autotaskApi.update(instance);
    	} catch (SOAPException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error updating the entity", e);
    	} catch (AutotaskException e) {
    		throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error updating the entity", e);
    	}
		Json result = Json.map().set("id", updatedId);
		return result;
    }

	@EndpointFunction(name = "_delete")
	public Json delete(Json params) {
		logger.info("Calling _delete, params: " + params.toString());
        EntityType entityType;
        Entity instance;
        try {
            entityType = EntityType.getEntityTypeByName(params.string("entity"));
            instance = entityType.newInstance();
            instance.fromJson(params);
        } catch (IllegalArgumentException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error parsing the entity", e);
        }
        try {
            autotaskApi.delete(instance);
        } catch (SOAPException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error deleting the entity", e);
        } catch (AutotaskException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "There was an error deleting the entity", e);
        }
        Json result = Json.map().set("id", instance.getId());
        return result;
	}

	@EndpointFunction(name = "_getEntity")
    public Json getEntity(Json params) {
        logger.info("Calling _getEntityFields, params: " + params.toString());
        EntityType entityType;
        try {
            entityType = EntityType.getEntityTypeByName(params.string("entity"));
        } catch (IllegalArgumentException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "Invalid entity name", e);
        }
        return entityType.getInfo().toJson();
    }

	@EndpointFunction(name = "_getEntityFields")
    public Json getEntityFields(Json params) {
        logger.info("Calling _getEntityFields, params: " + params.toString());
        EntityType entityType;
        try {
            entityType = EntityType.getEntityTypeByName(params.string("entity"));
        } catch (IllegalArgumentException e) {
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "Invalid entity name", e);
        }
        List<EntityFieldInfo> fieldInfoList = entityType.getInfo().getFields();
        return Json.list(fieldInfoList, fieldInfo -> fieldInfo.toJson());
    }


    @EndpointFunction(name = "_getWebUrl")
    public Json getWebUrl(FunctionRequest request) {
        Json params = request.getJsonParams();
        logger.info("Calling _getWebUrl, params: " + params.toString());
        return Json.map().set("webUrl", autotaskApi.getDefaultWebUrl());
    }

}
