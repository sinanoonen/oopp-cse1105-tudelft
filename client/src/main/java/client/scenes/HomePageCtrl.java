package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

/**
 * A controller for the home page scene
 */
public class HomePageCtrl {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;

    @Inject
    public HomePageCtrl(ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    public void refresh() {

    }
}
