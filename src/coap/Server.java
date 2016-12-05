/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coap;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
/**
 *
 * @author user
 */
public class Server extends CoapServer {
        
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
    /*
     * Application entry point.
     */
    public static void main(String[] args) throws SocketException {
        
        // create server
        Server server = new Server();
        // add endpoints on all IP addresses
        server.addEndpoints();
        server.start();
    }

    /**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
     */
    private void addEndpoints() {
        EndpointManager.getEndpointManager().getNetworkInterfaces().stream().filter((addr) -> (addr instanceof Inet4Address || addr.isLoopbackAddress())).map((addr) -> new InetSocketAddress(addr, COAP_PORT)).forEachOrdered((InetSocketAddress bindToAddress) -> {
            addEndpoint(new CoapEndpoint(bindToAddress));
        }); // only binds to IPv4 addresses and localhost
    }

    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public Server() throws SocketException {
        
            // provide an instance of a Hello-World resource
            add(new camera());
    }

    /*
     * Definition of the Hello-World Resource
     */
    class camera extends CoapResource {
        private String msgGET = new String();
        public String getmsgGET() {
            return msgGET;
        }
        public void setmsgGET(String msgGET) {
            this.msgGET=msgGET;
        }
        public camera() {
            super("camera");
           
        }
        @Override
        public void handleGET(CoapExchange exchange) {
           
            // respond to the request
            exchange.respond(ResponseCode.CONTENT,this.getmsgGET());
        }
        @Override
        public void handlePUT(CoapExchange exchange) {
            String resp = exchange.getRequestText();
            if(resp.equals("safe")) {
                exchange.respond(ResponseCode.CHANGED,"safe");
                this.setmsgGET("safe");
            }
            else {
                exchange.respond(ResponseCode.CHANGED,"unsafe");
                this.setmsgGET("unsafe");
            }
            changed();
        }
        @Override
        public void handleDELETE(CoapExchange exchange) {
            super.handleDELETE(exchange);
        }
    }
}