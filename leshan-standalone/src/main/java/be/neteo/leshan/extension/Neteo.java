package be.neteo.leshan.extension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.standalone.servlet.json.ClientSerializer;
import org.eclipse.leshan.standalone.servlet.json.LwM2mNodeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class Neteo {

    private static final long TIMEOUT = 20000; // ms

    protected static final Logger LOG = LoggerFactory.getLogger(Neteo.class);

    private final Gson gson;
    private final LwM2mServer server;
    protected final NeteoClient neteoClient;

    private final NeteoAPIv1 neteoAPIv1 = new NeteoAPIv1(this);
    private final NeteoAPIv2 neteoAPIv2 = new NeteoAPIv2(this);


    public Neteo(LeshanServer server) {
        LOG.info("extension Neteo used");


        String configFile = System.getenv("neteo.config.file");
        if (configFile == null)
            configFile = "./leshan-standalone/Neteo.properties";

        Properties properties = new Properties();
        try {

            InputStream input = new FileInputStream(configFile);
            properties.load(input);
            LOG.info("Loading standard properties from file [" + configFile + "]");
        } catch (IOException e) {
            LOG.warn("Unable to load standard properties from file [" + configFile + "]", e);
        }

        this.server = server;

        this.neteoClient = new NeteoClient(properties.getProperty("api.host", "localhost"));

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Client.class, new ClientSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        this.gson = gsonBuilder.create();


        server.getClientRegistry().addListener(this.neteoAPIv1);
        server.getObservationRegistry().addListener(this.neteoAPIv1);

        //server.getClientRegistry().addListener(this.neteoAPIv2);

    }

    protected LwM2mResource readResource(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        String target = "/" + objectId + "/" + objectInstanceId + "/" + resourceId;
        ReadRequest request = new ReadRequest(target);
        ValueResponse cResponse = this.server.send(client, request, TIMEOUT);

        return (LwM2mResource) cResponse.getContent();
    }

    protected <T extends LwM2mNode> T read(Client client, String url) {
        ReadRequest request = new ReadRequest(url);
        ValueResponse cResponse = this.server.send(client, request, TIMEOUT);

        return (T) cResponse.getContent();
    }

    protected boolean observeResource(Client client, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        String target = "/" + objectId + "/" + objectInstanceId + "/" + resourceId;
        ObserveRequest request = new ObserveRequest(target);
        LwM2mResponse cResponse = server.send(client, request, TIMEOUT);

        ResponseCode code = cResponse.getCode();
        return code.equals(ResponseCode.CONTENT);
    }

}
