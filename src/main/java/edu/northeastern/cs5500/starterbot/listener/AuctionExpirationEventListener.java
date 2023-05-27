package edu.northeastern.cs5500.starterbot.listener;

import edu.northeastern.cs5500.starterbot.auctionExpirationHandler.AuctionExpirationHandler;
import edu.northeastern.cs5500.starterbot.controller.AuctionController;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
public class AuctionExpirationEventListener extends ListenerAdapter {

    @Inject Set<AuctionExpirationHandler> auctionExpirationHandlers;
    @Inject AuctionController auctionController;

    /**
     * Instantiates a new Auction expiration event listener.
     *
     * @param jda the jda
     */
    @Inject
    public AuctionExpirationEventListener(JDA jda) {
        super();
        log.info("Set up auction expiration event listener for jda now");
        jda.addEventListener(this);
    }

    /**
     * On auction expiration event.
     *
     * @param auctionItem the auction item
     */
    // Expiration handler will handle three handler becuase they are all the child class of
    public void onAuctionExpirationEvent(AuctionItem auctionItem) {
        log.info(
                "we are on auction expiration handler, and try to get the latest version of item.");
        auctionItem = auctionController.getItemById(auctionItem.getId());
        if (auctionItem == null) {
            return;
        }
        log.info("auction item status is :" + auctionItem.getAuctionStatus());
        if (auctionItem.getAuctionStatus().equals("WITHDRAW")) {
            log.info("Item has been withdrawn, give up operation.");
            return;
        }
        for (AuctionExpirationHandler handler : auctionExpirationHandlers) {
            handler.onAuctionExpiration(auctionItem);
        }
    }
}
