package com.skylaneoptics.onem2m.ipe.lwm2m;

import com.skylaneoptics.ipso.SmartObject;
import com.skylaneoptics.ipso.SmartObjects;
import com.skylaneoptics.onem2m.io.OneM2mEncoder;
import com.skylaneoptics.utils.Tuple;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.leshan.LinkObject;
import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.DiscoverRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.DiscoverResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.observation.ObservationRegistryListener;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.obix.*;
import org.eclipse.om2m.commons.obix.io.ObixEncoder;

import org.onem2m.xml.protocols.Ae;
import org.onem2m.xml.protocols.Cin;
import org.onem2m.xml.protocols.Cnt;
import org.onem2m.xml.protocols.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * oneM2M Interworking Proxy Entity (IPE) for LWM2M.
 *
 * @author Pierre Saelens
 */
public class LwM2mInterworkingService implements ClientRegistryListener, ObservationRegistryListener {

    private static final Logger LOG = LoggerFactory.getLogger(LwM2mInterworkingService.class);

    LeshanServer leshanServer;
    Server httpServer;
    CloseableHttpClient httpClient;

    private Properties properties = new Properties();
    private final String baseUrl;

    public LwM2mInterworkingService(LeshanServer leshanServer, Server httpServer) {
        this.httpClient = HttpClients.createDefault();
        this.leshanServer = leshanServer;
        this.httpServer = httpServer;

        this.leshanServer.getClientRegistry().addListener(this);
        this.leshanServer.getObservationRegistry().addListener(this);

        String propFileName = "config.properties";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propFileName);
            properties.load(inputStream);

        } catch (Exception e) {
            LOG.warn("property file '" + propFileName + "' not found.");

            try {
                inputStream = this.getClass().getResourceAsStream("/" + propFileName);
                properties.load(inputStream);
            } catch (Exception e2) {
                LOG.warn("property file '" + propFileName + "' not found from classpath.");
            }
        } finally {
            if (inputStream != null) {
                try {
                    // silently close the stream
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        this.baseUrl = properties.getProperty("org.eclipse.om2m.cseBaseProtocol.default") + "://" + properties.get("org.eclipse.om2m.cseBaseAddress") + ":" + properties.get("org.eclipse.om2m.cseBasePort");
    }

    // -------------------------------------- ClientRegistryListener

    @Override
    public void registered(Client client) {
        LOG.info("Registering client [{}] with links [{}] ...", client.getEndpoint(), toString(client.getObjectLinks()));

        Ae ae = new Ae();

        // resourceName
        ae.setRn(client.getEndpoint());
        // App-Id
        ae.setApi(client.getRegistrationId());
        // requestReachability
        ae.setRr(false);
        // labels
        //ae.getLbl().add("protocol/lwm2m");

        createResource(ae, aeTargetId(), ResourceType.AE); //SP relative structured /~/cse-id/cse-name

        for (Tuple<SmartObject, Obj> tuple : toObix(client, client.getObjectLinks())) {
            SmartObject smartObject = tuple.getFirst();
            // create Container representing the SmartObject
            Cnt cnt = new Cnt();
            String smartObjectName = normalise(smartObject.getName()) + "-" + smartObject.getInstanceId();
            cnt.setRn(smartObjectName);
            //cnt.getLbl().add("type/smartobject");

            createResource(cnt, cntTargetId(client), ResourceType.CONTAINER);

            // create Container + content Instance for the Descriptor
            cnt = new Cnt();
            cnt.setRn("DESCRIPTOR");
            //cnt.getLbl().add("type/device");

            createResource(cnt, cntTargetId(client, smartObjectName), ResourceType.CONTAINER);

            // content instance
            Cin cin = new Cin();
            //cin.setRn("cin_" + client.getRegistrationId());
            cin.setCnf(MimeMediaType.OBIX);

            cin.setCon(ObixEncoder.toString(tuple.getSecond()));
            //cin.getLbl().add("type/device");

            createResource(cin, cinTargetId(client, smartObjectName, "DESCRIPTOR"), ResourceType.CONTENT_INSTANCE);

            // create Container for the Data
            cnt = new Cnt();
            cnt.setRn("DATA");
            //cnt.getLbl().add("type/device");

            createResource(cnt, cntTargetId(client, smartObjectName), ResourceType.CONTAINER);
        }
    }

    private String toString(LinkObject[] objectLinks) {
        if (objectLinks == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (LinkObject link : objectLinks) {
            sb.append(link.getUrl()).append(" ");
        }
        return sb.toString();
    }


    @Override
    public void updated(Client clientUpdated) {

    }

    @Override
    public void unregistered(Client client) {
        deleteResource(cntTargetId(client));
    }

    // -------------------------------------- ObservationRegistryListener


    @Override
    public void cancelled(Observation observation) {

    }

    @Override
    public void newValue(Observation observation, LwM2mNode value) {
        Client client = leshanServer.getClientRegistry().findByRegistrationId(observation.getRegistrationId());

        observation.getPath();
        client.getEndpoint();

        Tuple<SmartObject, Obj> tuple = toObix(client, observation, value);

        if (tuple != null) {
            SmartObject smartObject = tuple.getFirst();
            Obj obj = tuple.getSecond();

            String smartObjectName = normalise(smartObject.getName()) + "-" + smartObject.getInstanceId();

            // content instance
            Cin cin = new Cin();
            //cin.setRn("cin_" + client.getRegistrationId());
            cin.setCnf(MimeMediaType.OBIX);

            cin.setCon(ObixEncoder.toString(obj));
            //cin.getLbl().add("type/device");

            createResource(cin, cinTargetId(client, smartObjectName, "DATA"), ResourceType.CONTENT_INSTANCE);

        }

    }

    @Override
    public void newObservation(Observation observation) {

    }

    // --------------------------------------------------------------------- oneM2M Protocol over HTTP Binding
    // -----------------------------------------------------------------------------------------------------------
    private String cinTargetId(Client client, String smartObjectName, String containerName) {
        return String.format("/~/%s/%s/%s/%s/%s", properties.get("org.eclipse.om2m.cseBaseId"), properties.get("org.eclipse.om2m.cseBaseName"), client.getEndpoint(), smartObjectName, containerName);
    }

    private String cntTargetId(Client client, String smartObjectName) {
        return String.format("/~/%s/%s/%s/%s", properties.get("org.eclipse.om2m.cseBaseId"), properties.get("org.eclipse.om2m.cseBaseName"), client.getEndpoint(), smartObjectName);
    }

    private String cntTargetId(Client client) {
        return String.format("/~/%s/%s/%s", properties.get("org.eclipse.om2m.cseBaseId"), properties.get("org.eclipse.om2m.cseBaseName"), client.getEndpoint());
    }

    private String aeTargetId() {
        return String.format("/~/%s/%s", properties.get("org.eclipse.om2m.cseBaseId"), properties.get("org.eclipse.om2m.cseBaseName"));
    }

    private void createResource(Resource resource, String targetId, int type) {
        CloseableHttpResponse response = null;
        try {
            String uri = baseUrl + targetId;

            LOG.info("Creating resource [" + resource.getRn() + "] (target: " + uri + ") ...");

            HttpPost httpPost = new HttpPost(uri);


            StringEntity input = new StringEntity(OneM2mEncoder.toString(resource));
            input.setContentType("application/xml;ty=" + type);

            httpPost.addHeader("X-M2M-Origin", "admin:admin");

            httpPost.setEntity(input);
            response = this.httpClient.execute(httpPost);

            LOG.debug("" + response.getStatusLine());

            if (LOG.isDebugEnabled()) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String output;
                LOG.debug("Response from Server:");
                while ((output = br.readLine()) != null) {
                    LOG.debug(output);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteResource(String targetId) {
        CloseableHttpResponse response = null;
        try {
            String uri = baseUrl + targetId;

            HttpDelete httpDelete = new HttpDelete(uri);
            httpDelete.addHeader("X-M2M-Origin", "admin:admin");

            response = this.httpClient.execute(httpDelete);

            System.out.println(response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // --------------------------------------------------------------------- IPSO Smart Object over CoAP Protocol
    // -----------------------------------------------------------------------------------------------------------
    private static final long TIMEOUT = 20000; // ms


    private String readString(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        try {
            return (String) this.readResource(client, objectId, objectInstanceId, resourceId).getValue();
        } catch (Exception e) {
            // unable to read resource
            return "";
        }
    }

    private boolean readBoolean(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        try {
            return (Boolean) this.readResource(client, objectId, objectInstanceId, resourceId).getValue();
        } catch (Exception e) {
            // unable to read resource
            return false;
        }
    }

    private int readInteger(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        try {
            return (Integer) this.readResource(client, objectId, objectInstanceId, resourceId).getValue();
        } catch (Exception e) {
            // unable to read resource
            return -1;
        }
    }

    private float readFloat(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        try {
            return (Float) this.readResource(client, objectId, objectInstanceId, resourceId).getValue();
        } catch (Exception e) {
            // unable to read resource
            return -1;
        }
    }

    private LinkObject[] discoverResources(Client client, String target) {

        if ("false".equals(properties.getProperty("com.skylaneoptics.onem2m.ipe.lwm2m.discovery", "true")))
            return new LinkObject[0];

        LOG.info("Discovering resources [endpoint: {}, address: {}, target: {}] ...", client.getEndpoint(), client.getAddress().toString(), target);

        DiscoverRequest request = new DiscoverRequest(target);
        DiscoverResponse response = this.leshanServer.send(client, request, TIMEOUT);

        // TODO check why no response from the discovery request to the SensorTag (JIRA: https://jira.senzor.be/browse/SEN-168)
        if (response == null)
            return new LinkObject[0];

        LOG.info("Response from discovering request: [{}]", response.getCode());

        LinkObject[] objectLinks = response.getObjectLinks();

        LOG.info("Discovered resources: [{}].", toString(objectLinks));

        return objectLinks == null ? new LinkObject[0] : objectLinks;
    }

    protected LwM2mResource readResource(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        String target = "/" + objectId + "/" + objectInstanceId + "/" + resourceId;
        ReadRequest request = new ReadRequest(target);
        ReadResponse cResponse = this.leshanServer.send(client, request, TIMEOUT);

        LwM2mResource node = (LwM2mResource) cResponse.getContent();

        return node;
    }

    protected boolean observeResource(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        String target = "/" + objectId + "/" + objectInstanceId + "/" + resourceId;
        ObserveRequest request = new ObserveRequest(target);
        ObserveResponse cResponse = this.leshanServer.send(client, request, TIMEOUT);

        ResponseCode code = cResponse.getCode();
        return code.equals(ResponseCode.CONTENT);
    }

    // --------------------------------------------------------------------- Utils
    // -----------------------------------------------------------------------------------------------------------
    private Tuple<SmartObject, Obj> toObix(Client client, Observation observation, LwM2mNode value) {
        SmartObject smartObject = SmartObjects.get(observation.getPath().getObjectId());

        if (smartObject != null) {
            if (observation.getPath().getObjectInstanceId() == null) {
                // TODO handle observation on object id
                return null;
            }

            smartObject = smartObject.instance(observation.getPath().getObjectInstanceId());

            if (value instanceof LwM2mSingleResource) {
                // handle single resource observation.
                // TODO handle observation on objectInstanceId
                // TODO handle observation on multiple resource
                LwM2mSingleResource resource = (LwM2mSingleResource) value;

                // content instance
                Cin cin = new Cin();
                //cin.setRn("cin_" + client.getRegistrationId());
                cin.setCnf(MimeMediaType.OBIX);

                Obj obj = new Obj();
                obj.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + observation.getPath());
                switch (resource.getType()) {
                    case BOOLEAN:
                        obj.add(new Bool("value", (Boolean) resource.getValue()));
                        break;
                    case INTEGER:
                        obj.add(new Int("value", (Integer) resource.getValue()));
                        break;
                    case FLOAT:
                        obj.add(new Real("value", (Double) resource.getValue()));
                        break;
                    case STRING:
                        obj.add(new Str("value", (String) resource.getValue()));
                        break;
                }

                return new Tuple<>(smartObject, obj);
            }
        }
        return null;
    }

    private Collection<Tuple<SmartObject, Obj>> toObix(Client client, LinkObject[] links) {
        HashMap<String, Tuple<SmartObject, Obj>> instances = new HashMap<>();

        for (LinkObject link : links) {
            // The list of Objects and Object Instances is included in the payload of the registration message (cf. LWM2M specifications)
            if (link.getObjectId() == null || link.getObjectInstanceId() == null || link.getResourceId() != null)
                continue;

            SmartObject smartObject = SmartObjects.get(link.getObjectId());

            LOG.info("Found related smart object [" + smartObject.getName() + "] for link [" + link.getUrl() + "].");


            Tuple<SmartObject, Obj> tuple = instances.get(link.getUrl());
            if (tuple == null) {
                SmartObject instance = smartObject.instance(link.getObjectInstanceId());
                tuple = new Tuple<>(instance, new Obj());
                tuple.getSecond().setIs(new Contract("ipso:" + smartObject.getId()));
                tuple.getSecond().setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + "/" + link.getUrl());
                instances.put(link.getUrl(), tuple);
            }

            Obj obj = tuple.getSecond();

            // Discover resources under the current link
            LinkObject[] resources = discoverResources(client, link.getUrl());

            if (resources.length == 0) {
                // no resource discovered, populate with default resource definitions
                for (com.skylaneoptics.ipso.Resource resource : smartObject.getResourcedefs()) {
                    populateObj(obj, client, link, resource);
                }
            }

            for (LinkObject resourceLink : resources) {
                com.skylaneoptics.ipso.Resource resource = getResourceFromLinkObject(resourceLink, smartObject);
                if (resource != null) {
                    populateObj(obj, client, link, resource);
                }

            }
        }


        return instances.values();
    }

    private void populateObj(Obj obj, Client client, LinkObject link, com.skylaneoptics.ipso.Resource resource) {
        //if ((link.getResourceId() != null && link.getResourceId() == resource.getId())) {
                    /*if (resource.getOperations().contains("R")) {
                        // OP Read
                        Op opRead = new Op();
                        opRead.setName("read");
                        opRead.setWritable(false);
                        opRead.setDisplay(resource.getDescription());
                        opRead.setDisplayName(resource.getName());
                        opRead.setHref(new Uri(httpServer.getURI() + "api/clients/" + endpoint + link.getUrl()));
                        opRead.setIs(new Contract("retrieve"));
                        opRead.setIn(new Contract("obix:Nil"));
                        opRead.setOut(new Contract("obix:Nil"));
                        obj.add(opRead);
                    }*/

        if ("string".equalsIgnoreCase(resource.getType())) {
            Str str = new Str(resource.getName(), readString(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
            str.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
            str.setWritable(resource.getOperations().contains("W"));
            obj.add(str);
        } else if ("integer".equalsIgnoreCase(resource.getType())) {
            Int anInt = new Int(resource.getName(), readInteger(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
            anInt.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
            anInt.setWritable(resource.getOperations().contains("W"));
            obj.add(anInt);
        } else if ("boolean".equalsIgnoreCase(resource.getType())) {
            Bool bool = new Bool(resource.getName(), readBoolean(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
            bool.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
            bool.setWritable(resource.getOperations().contains("W"));
            obj.add(bool);
        } else if ("float".equalsIgnoreCase(resource.getType())) {
            Real real = new Real(resource.getName(), readFloat(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
            real.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
            obj.add(real);
        }
    }

    private com.skylaneoptics.ipso.Resource getResourceFromLinkObject(LinkObject resourceLink, SmartObject smartObject) {
        if (resourceLink.getResourceId() != null) {
            for (com.skylaneoptics.ipso.Resource resource : smartObject.getResourcedefs()) {
                if (resource.getId() == resourceLink.getResourceId())
                    return resource;
            }
        }

        return null;
    }

    private String normalise(String value) {
        return value.replaceAll("\\s+", "-")
                .replaceAll("[^-a-zA-Z0-9]", "");
    }
}
