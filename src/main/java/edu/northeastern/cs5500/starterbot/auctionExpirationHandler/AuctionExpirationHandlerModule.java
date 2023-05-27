package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public class AuctionExpirationHandlerModule {

    /**
     * Bind notify owner expiration handler auction expiration handler.
     *
     * @param handler the handler
     * @return the auction expiration handler
     */
    @Provides
    @IntoSet
    AuctionExpirationHandler bindNotifyOwnerExpirationHandler(
            NotifyOwnerExpirationHandler handler) {
        return handler;
    }

    /**
     * Bind notify channel expiration handler auction expiration handler.
     *
     * @param handler the handler
     * @return the auction expiration handler
     */
    @Provides
    @IntoSet
    AuctionExpirationHandler bindNotifyChannelExpirationHandler(
            NotifyChannelExpirationHandler handler) {
        return handler;
    }

    /**
     * Bind close expiration handler auction expiration handler.
     *
     * @param handler the handler
     * @return the auction expiration handler
     */
    @Provides
    @IntoSet
    AuctionExpirationHandler bindCloseExpirationHandler(CloseExpirationHandler handler) {
        return handler;
    }
}
