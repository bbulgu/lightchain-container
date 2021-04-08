package underlay;

import underlay.packets.Request;
import underlay.packets.Response;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Represents the underlay layer of the skip-graph DHT. Handles node-to-node communication.
 */
public abstract class Underlay {

    private int port;
    private String address;
    private String fullAddress;

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String getFullAddress() {
        return fullAddress;
    }


    /**
     * Initializes the underlay.
     * @param port the port that the underlay should be bound to.
     * @return true iff the initialization was successful.
     */
    public final boolean initialize(int port) {
        this.port = port;
        try {
            address = Inet4Address.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            System.err.println("[Underlay] Could not acquire the local host name during initialization.");
            e.printStackTrace();
            return false;
        } catch (Exception e){
            // TODO: add more context
            return false;
        }
        fullAddress = address + ":" + port;
        return initUnderlay(port);
    }

    /**
     * Contains the underlay-specific initialization procedures.
     * @param port the port that the underlay should be bound to.
     * @return true iff the initialization was successful.
     */
    protected abstract boolean initUnderlay(int port);

    /**
     * Can be used to send a request to a remote server that runs the same underlay architecture.
     * @param address address of the remote server.
     * @param port port of the remote server.
     * @param request the request.
     * @return response emitted by the remote server.
     */
    public abstract Response sendMessage(String address, int port, Request request);


    /**
     * Terminates the underlay.
     * @return true iff the termination was successful.
     */
    public abstract boolean terminate();

    public Response dispatchRequest(Request request) {
        return middleLayer.receive(request);
    }


}