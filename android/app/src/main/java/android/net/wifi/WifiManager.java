package android.net.wifi;

import java.util.List;

/**
 * Created by lin.mq on 2016/7/12.
 */
public class WifiManager {

    public static final String WIFI_HOTSPOT_CLIENTS_CHANGED_ACTION = "android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED";

    public static final int WIFI_STATE_UNKNOWN = 4;
    public static final int WIFI_STATE_ENABLING = 2;
    public static final int WIFI_STATE_ENABLED = 3;

    /**
     * Block the client
     *
     * @param client The hotspot client to be blocked
     * @return {@code true} if the operation succeeds else {@code false}
     */
    public boolean blockClient(HotspotClient client) {
        return false;
    }

    /**
     * Unblock the client
     *
     * @param client The hotspot client to be unblocked
     * @return {@code true} if the operation succeeds else {@code false}
     */
    public boolean unblockClient(HotspotClient client) {
        return false;
    }

    /**
     * Return the hotspot clients
     *
     * @return a list of hotspot client in the form of a list
     * of {@link HotspotClient} objects.
     */
    public List<HotspotClient> getHotspotClients() {
        return null;
    }

    /**
     * Return the IP address of the client
     *
     * @param deviceAddress The mac address of the hotspot client
     */
    public String getClientIp(String deviceAddress) {
        return null;
    }

    public int getWifiState() {
        return WIFI_STATE_UNKNOWN;
    }

    public boolean setWifiEnabled(boolean enabled) {
        return false;
    }
}
