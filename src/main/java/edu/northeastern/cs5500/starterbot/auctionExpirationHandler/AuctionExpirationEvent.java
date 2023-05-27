package edu.northeastern.cs5500.starterbot.auctionExpirationHandler;

import edu.northeastern.cs5500.starterbot.listener.AuctionExpirationEventListener;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;

public class AuctionExpirationEvent extends Event {
    private final AuctionItem auctionItem;

    private AuctionExpirationEventListener listener;

    /**
     * Constructor, initialize the auctionExpirationEven with jda and AuctionItem. Attach the
     * AuctionItem with this event and register the auctionexpiration event listener with this event
     *
     * @param jda the jda
     * @param item the item
     */
    public AuctionExpirationEvent(@Nonnull JDA jda, AuctionItem item) {
        super(jda);
        auctionItem = item;

        List<Object> listeners = jda.getEventManager().getRegisteredListeners();
        for (Object lis : listeners) {
            if (lis instanceof AuctionExpirationEventListener) {
                this.listener = (AuctionExpirationEventListener) lis;
                break;
            }
        }
    }

    /** Trigger expirationEvent */
    public void fireEventNow() {
        listener.onAuctionExpirationEvent(auctionItem);
    }

    /**
     * Fire event after delay.
     *
     * @param delayInSeconds the delay in seconds
     */
    public void fireEventAfterDelay(int delayInSeconds) {
        CompletableFuture.runAsync(
                () -> {
                    try {
                        Thread.sleep(delayInSeconds * 1000);
                    } catch (InterruptedException e) { // NOSONAR
                        e.printStackTrace();
                    }
                    listener.onAuctionExpirationEvent(auctionItem);
                });
    }
}
