package com.mahay.mchat.im;

import com.mahay.mchat.im.inf.IMService;

/**
 * Factory class that produces IMService implementation
 */
public class IMServiceFactory {
    public static IMService getIMService() {
        return TCPIMService.getInstance();
    }
}
