package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.Paginator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.bson.types.ObjectId;

public class PaginatorController {

    static final int MAXIMUM_PAGINATORS = 10;
    List<Paginator> paginators;
    Map<String, Paginator> paginatorById;

    /** Instantiates a new Paginator controller. */
    @Inject
    PaginatorController() {
        paginators = new ArrayList<>(MAXIMUM_PAGINATORS + 1);
        paginatorById = new HashMap<>(MAXIMUM_PAGINATORS + 1);
    }

    /** Ensure that only MAXIMUM_PAGINATORS are stored by expiring the oldest paginator. */
    void trimPaginators() {
        while (paginators.size() > MAXIMUM_PAGINATORS) {
            // Get the oldest paginator
            Paginator toExpire = paginators.remove(0);
            paginatorById.remove(toExpire.getId().toString());
        }
    }

    /**
     * Create paginator paginator.
     *
     * @param auctionItems the auction items
     * @param itemsPerPage the items per page
     * @return the paginator
     */
    @Nonnull
    public Paginator createPaginator(List<AuctionItem> auctionItems, int itemsPerPage) {
        Paginator paginator = new Paginator(auctionItems, itemsPerPage);
        paginator.setId(new ObjectId());
        this.paginators.add(paginator);
        trimPaginators();
        this.paginatorById.put(paginator.getId().toString(), paginator);
        return paginator;
    }

    /**
     * Gets paginator.
     *
     * @param paginatorId the paginator id
     * @return the paginator
     */
    @Nonnull
    public Paginator getPaginator(String paginatorId) {
        return this.paginatorById.get(paginatorId);
    }
}
