package com.mahay.mchat.im;

/**
 * Factory class that produces IMService implementation
 */
public class IMServiceFactory {
    public static IMService getIMService() {
        return TCPIMService.getInstance();
    }
}
