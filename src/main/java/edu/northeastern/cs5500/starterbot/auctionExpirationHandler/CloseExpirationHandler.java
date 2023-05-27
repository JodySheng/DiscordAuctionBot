package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CloseExpirationHandler implements AuctionExpirationHandler {

    @Inject AuctionController auctionController;

    /** Instantiates a new Close expiration handler. */
    @Inject
    public CloseExpirationHandler() {
        // public and empty for dagger
    }

    @Override
    public void onAuctionExpiration(@NotNull AuctionItem auctionItem) {
        if (auctionItem.getAuctionStatus().equals("WITHDRAW")) {
            log.info("Item has been withdrawn, give up operation.");
            return;
        }

        log.info("Setting the auction item to EXPIRED");
        auctionController.finishAuction(auctionItem);
    }
}
