package io.slingr.endpoints.autotask.polling;

import io.slingr.endpoints.autotask.ws.*;
import io.slingr.endpoints.utils.Json;
import org.apache.log4j.Logger;

import javax.xml.soap.SOAPException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollingService {
    private static final Logger logger = Logger.getLogger(PollingService.class);

    private int frequencyInMinutes;
    private List<EntityType> entitiesToPoll;
    private EventSender eventSender;
    private AutotaskApi autotaskApi;

    public PollingService(int frequencyInMinutes, List<EntityType> entitiesToPoll, EventSender eventSender, AutotaskApi autotaskApi) {
        this.frequencyInMinutes = frequencyInMinutes;
        this.entitiesToPoll = entitiesToPoll;
        this.eventSender = eventSender;
        this.autotaskApi = autotaskApi;
    }

    public void run() {
        logger.info(String.format("Initializing polling service to run every [%s] minutes", frequencyInMinutes));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable periodicTask = new Runnable() {
            public void run() {
                try {
                    logger.info("Polling entities");
                    Date newLastPolling = null;
                    if (entitiesToPoll != null) {
                        for (EntityType entityType : entitiesToPoll) {
                            String queryField = null;
                            if (entityType.getModifiedField() != null) {
                                queryField = entityType.getModifiedField();
                            } else if (entityType.getCreateField() != null) {
                                queryField = entityType.getCreateField();
                            } else {
                                logger.warn(String.format("Changes cannot be detected on entity [%s]", entityType.getName()));
                            }
                            if (queryField != null) {
                                boolean moreRecords;
                                newLastPolling = new Date();
                                Long lastId = entityType.getLastPollingId();
                                do {
                                    QueryBuilder queryBuilder = new QueryBuilder(entityType);
                                    queryBuilder.addFilter(queryField, false, "GreaterThan", DateHelper.convertToDateTime(entityType.getEffectiveLastPolling()));
                                    if (lastId != null) {
                                        queryBuilder.addFilter("id", false, "GreaterThan", lastId.toString());
                                    }
                                    List<Entity> entities = (List<Entity>) autotaskApi.query(queryBuilder);
                                    for (Entity entity : entities) {
                                        Json event = Json.map();
                                        event.set("entityType", entityType.getName());
                                        event.set("record", entity.toJson());
                                        eventSender.sendEvent("recordChange", event);
                                    }
                                    moreRecords = entities.size() >= 500;
                                    lastId = entities.isEmpty() ? lastId : entities.get(entities.size() - 1).getId();
                                } while (moreRecords);
                                entityType.setLastPolling(newLastPolling);
                                if (entityType.getModifiedField() == null && entityType.getCreateField() != null) {
                                    // we only have to set this when polling by create time due to limitations (create dates are truncated)
                                    entityType.setLastPollingId(lastId);
                                }
                            }
                        }
                    }
                    logger.info("Done polling entities");
                } catch (SOAPException e) {
                    logger.error("Error when querying Autotask to detect changes", e);
                } catch (Exception e) {
                    logger.error("Error polling Autotaks to detect changes", e);
                }
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 1, frequencyInMinutes, TimeUnit.MINUTES);
    }
}
