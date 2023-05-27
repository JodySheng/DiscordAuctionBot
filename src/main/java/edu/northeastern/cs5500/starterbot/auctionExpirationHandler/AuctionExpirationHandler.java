package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import javax.annotation.Nonnull;

public interface AuctionExpirationHandler {

    /**
     * On auction expiration.
     *
     * @param auctionItem the auction item
     */
    public void onAuctionExpiration(@Nonnull AuctionItem auctionItem);
}
