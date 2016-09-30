package com.skylaneoptics.onem2m.ipe.lwm2m;

import com.skylaneoptics.onem2m.io.OneM2mEncoder;
import com.skylaneoptics.onem2m.io.OneM2mMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.obix.io.ObixEncoder;
import org.eclipse.om2m.commons.obix.Obj;
import org.eclipse.om2m.commons.obix.Str;

import org.onem2m.xml.protocols.Ae;
import org.onem2m.xml.protocols.Cin;
import org.onem2m.xml.protocols.Cnt;
import org.onem2m.xml.protocols.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * oneM2M Interworking Proxy Entity (IPE) for LWM2M.
 *
 * @author Pierre Saelens
 */
public class LwM2mInterworkingService implements ClientRegistryListener {

    LeshanServer leshanServer;
    CloseableHttpClient httpClient;

    private final String baseUrl = "http://127.0.0.1:8282";

    public LwM2mInterworkingService(LeshanServer leshanServer) {
        this.httpClient = HttpClients.createDefault();
        this.leshanServer = leshanServer;

        this.leshanServer.getClientRegistry().addListener(this);

    }

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

        // container
        Cnt cnt = new Cnt();
        cnt.setRn("DESCRIPTOR");
        //cnt.getLbl().add("type/device");

        createResource(cnt, "/~/mn-cse/mn-cse/" + client.getEndpoint(), ResourceType.CONTAINER);

        // content instance
        Cin cin = new Cin();
        cin.setRn("cin_" + client.getRegistrationId());
        cin.setCnf("application/obix");

        Obj obj = new Obj();
        obj.add(new Str("manufacturer", readManufacturer(client)));
        obj.add(new Str("serialNumber", readSerialNumber(client)));
        obj.add(new Str("firmwareVersion", readFirmwareVersion(client)));

        cin.setCon(ObixEncoder.toString(obj));
        //cin.getLbl().add("type/device");

        createResource(cin, "/~/mn-cse/mn-cse/" + client.getEndpoint() + "/DESCRIPTOR", ResourceType.CONTENT_INSTANCE);

    }

    private void createResource(Resource resource, String targetId, int type) {
        CloseableHttpResponse response = null;
        try {
            String uri = baseUrl + targetId;

            System.out.println("Executing request " + uri + " + " + resource.getRn() + ".... \n");

            HttpPost httpPost = new HttpPost(uri);



            StringEntity input = new StringEntity(OneM2mEncoder.toString(resource));
            input.setContentType("application/xml;ty="+type);

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
        } catch(IOException e) {
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

    @Override
    public void updated(Client clientUpdated) {

    }

    @Override
    public void unregistered(Client client) {

    }

    private static final long TIMEOUT = 20000; // ms

    private String readManufacturer(Client client) {
        try {
            return (String) this.readResource(client, 3, 0, 0).getValue();
        } catch (Exception e) {
            // unable to read resource
            return "";
        }
    }

    private String readSerialNumber(Client client) {
        try {
            return (String) this.readResource(client, 3, 0, 2).getValue();
        } catch (Exception e) {
            // unable to read resource
            return "";
        }
    }

    private String readFirmwareVersion(Client client) {
        try {
            return (String) this.readResource(client, 3, 0, 3).getValue();
        } catch (Exception e) {
            // unable to read resource
            return "";
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

}
