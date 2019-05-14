package io.slingr.endpoints.autotask.ws;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class ZoneInfo {
    private SOAPMessage message;

    public ZoneInfo(SOAPMessage message) {
        this.message = message;
    }

    public String getUrl() throws SOAPException {
        return message.getSOAPBody().getElementsByTagName("URL").item(0).getTextContent();
    }
}
