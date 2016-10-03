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
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
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

import org.eclipse.om2m.commons.resource.AE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.onem2m.xml.protocols.Ae;
import org.onem2m.xml.protocols.Cin;
import org.onem2m.xml.protocols.Cnt;
import org.onem2m.xml.protocols.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * oneM2M Interworking Proxy Entity (IPE) for LWM2M.
 *
 * @author Pierre Saelens
 */
public class LwM2mInterworkingService implements ClientRegistryListener, ObservationRegistryListener {

    LeshanServer leshanServer;
    Server httpServer;
    CloseableHttpClient httpClient;

    private final String baseUrl = "http://127.0.0.1:8282";

    public LwM2mInterworkingService(LeshanServer leshanServer, Server httpServer) {
        this.httpClient = HttpClients.createDefault();
        this.leshanServer = leshanServer;
        this.httpServer = httpServer;

        this.leshanServer.getClientRegistry().addListener(this);
        this.leshanServer.getObservationRegistry().addListener(this);
    }

    // -------------------------------------- ClientRegistryListener

    @Override
    public void registered(Client client) {
        Ae ae = new Ae();

        // resourceName
        ae.setRn(client.getEndpoint());
        // App-Id
        ae.setApi(client.getRegistrationId());
        // requestReachability
        ae.setRr(false);
        // labels
        //ae.getLbl().add("protocol/lwm2m");

        createResource(ae, "/~/mn-cse/mn-cse", ResourceType.AE); //SP relative structured /~/cse-id/cse-name


        for (Tuple<SmartObject, Obj> tuple : toObix(client, client.getObjectLinks())) {
            SmartObject smartObject = tuple.getFirst();
            // create Container representing the SmartObject
            Cnt cnt = new Cnt();
            String smartObjectName = normalise(smartObject.getName()) + "-" + smartObject.getInstanceId();
            cnt.setRn(smartObjectName);
            //cnt.getLbl().add("type/smartobject");

            createResource(cnt, "/~/mn-cse/mn-cse/" + client.getEndpoint(), ResourceType.CONTAINER);

            // create Container + content Instance for the Descriptor
            cnt = new Cnt();
            cnt.setRn("DESCRIPTOR");
            //cnt.getLbl().add("type/device");

            createResource(cnt, "/~/mn-cse/mn-cse/" + client.getEndpoint() + "/" + smartObjectName, ResourceType.CONTAINER);

            // content instance
            Cin cin = new Cin();
            //cin.setRn("cin_" + client.getRegistrationId());
            cin.setCnf(MimeMediaType.OBIX);

            cin.setCon(ObixEncoder.toString(tuple.getSecond()));
            //cin.getLbl().add("type/device");

            createResource(cin, "/~/mn-cse/mn-cse/" + client.getEndpoint() + "/" + smartObjectName + "/DESCRIPTOR", ResourceType.CONTENT_INSTANCE);

            // create Container for the Data
            cnt = new Cnt();
            cnt.setRn("DATA");
            //cnt.getLbl().add("type/device");

            createResource(cnt, "/~/mn-cse/mn-cse/" + client.getEndpoint() + "/" + smartObjectName, ResourceType.CONTAINER);
        }
    }


    @Override
    public void updated(Client clientUpdated) {

    }

    @Override
    public void unregistered(Client client) {
        deleteResource("/~/mn-cse/mn-cse/" + client.getEndpoint());
    }

    // -------------------------------------- ObservationRegistryListener


    @Override
    public void cancelled(Observation observation) {

    }

    @Override
    public void newValue(Observation observation, LwM2mNode value) {

        System.out.println("Observation, Path: " + observation.getPath());
        System.out.println("Observation, RegistrationId: " + observation.getRegistrationId());

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

            createResource(cin, "/~/mn-cse/mn-cse/" + client.getEndpoint() + "/" + smartObjectName + "/DATA", ResourceType.CONTENT_INSTANCE);

        }

    }

    @Override
    public void newObservation(Observation observation) {

    }

    // --------------------------------------------------------------------- oneM2M Protocol over HTTP Binding
    // -----------------------------------------------------------------------------------------------------------
    private void createResource(Resource resource, String targetId, int type) {
        CloseableHttpResponse response = null;
        try {
            String uri = baseUrl + targetId;

            System.out.println("Executing request " + uri + " + " + resource.getRn() + ".... \n");

            HttpPost httpPost = new HttpPost(uri);


            StringEntity input = new StringEntity(OneM2mEncoder.toString(resource));
            input.setContentType("application/xml;ty=" + type);

            httpPost.addHeader("X-M2M-Origin", "admin:admin");

            httpPost.setEntity(input);
            response = this.httpClient.execute(httpPost);

            System.out.println(response.getStatusLine());

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
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
                        obj.add(new Real("value", (Float) resource.getValue()));
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
            if (link.getObjectId() == null || link.getObjectInstanceId() == null)
                continue;

            SmartObject smartObject = SmartObjects.get(link.getObjectId());

            if (smartObject == null)
                continue;

            Tuple<SmartObject, Obj> tuple = instances.get(link.getObjectId() + "/" + link.getObjectInstanceId());
            if (tuple == null) {
                SmartObject instance = smartObject.instance(link.getObjectInstanceId());
                tuple = new Tuple<>(instance, new Obj());
                tuple.getSecond().setIs(new Contract("ipso:" + smartObject.getId()));
                tuple.getSecond().setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + "/" + link.getObjectId() + "/" + link.getObjectInstanceId());
                instances.put(link.getObjectId() + "/" + link.getObjectInstanceId(), tuple);
            }

            Obj obj = tuple.getSecond();

            for (com.skylaneoptics.ipso.Resource resource : smartObject.getResourcedefs()) {
                if ((link.getResourceId() != null && link.getResourceId() == resource.getId())) {
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

                    if ("string".equals(resource.getType())) {
                        Str str = new Str(resource.getName(), readString(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
                        str.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
                        str.setWritable(resource.getOperations().contains("W"));
                        obj.add(str);
                    } else if ("integer".equals(resource.getType())) {
                        Int anInt = new Int(resource.getName(), readInteger(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
                        anInt.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
                        anInt.setWritable(resource.getOperations().contains("W"));
                        obj.add(anInt);
                    } else if ("boolean".equals(resource.getType())) {
                        Bool bool = new Bool(resource.getName(), readBoolean(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
                        bool.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
                        bool.setWritable(resource.getOperations().contains("W"));
                        obj.add(bool);
                    } else if ("float".equals(resource.getType())) {
                        Real real = new Real(resource.getName(), readFloat(client, link.getObjectId(), link.getObjectInstanceId(), link.getResourceId()));
                        real.setHref(httpServer.getURI() + "api/clients/" + client.getEndpoint() + link.getUrl());
                        obj.add(real);
                    }
                }

            }
        }
        return instances.values();
    }

    private String normalise(String value) {
        return value.replaceAll("\\s+", "-")
                .replaceAll("[^-a-zA-Z0-9]", "");
    }
}
