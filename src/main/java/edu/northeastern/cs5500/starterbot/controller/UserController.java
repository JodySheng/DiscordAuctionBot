package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.DiscordUser;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.bson.types.ObjectId;

public class UserController {

    GenericRepository<DiscordUser> discordUserRepository;
    GenericRepository<AuctionItem> itemRepository;

    /**
     * Instantiates a new User controller.
     *
     * @param discordUserRepository the discord user repository
     * @param itemRepository the item repository
     */
    @Inject
    UserController(
            GenericRepository<DiscordUser> discordUserRepository,
            GenericRepository<AuctionItem> itemRepository) {
        this.discordUserRepository = discordUserRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Update one user discord user.
     *
     * @param discordUser the discord user
     * @return the discord user
     */
    @Nonnull
    public DiscordUser updateOneUser(DiscordUser discordUser) {
        return discordUserRepository.update(discordUser);
    }

    /**
     * Add item to seller inventory discord user.
     *
     * @param discordUser the discord user
     * @param item the item
     * @return the discord user
     */
    public DiscordUser addItemToSellerInventory(DiscordUser discordUser, AuctionItem item) {
        List<ObjectId> sellerInventory = discordUser.getSellerInventory();
        if (sellerInventory == null) {
            sellerInventory = new ArrayList<>();
            sellerInventory.add(item.getId());
        } else {
            sellerInventory.add(item.getId());
        }
        discordUser.setSellerInventory(sellerInventory);
        return updateOneUser(discordUser);
    }

    /**
     * Add item to watch list.
     *
     * @param currentUserId the current user id
     * @param itemId the item id
     */
    public void addItemToWatchList(String currentUserId, ObjectId itemId) {
        DiscordUser discordUser = getDiscordUserByUserId(currentUserId);
        List<ObjectId> watchList = discordUser.getWatchList();
        if (watchList == null) {
            watchList = new ArrayList<>();
            watchList.add(itemId);
        } else if (!watchList.contains(itemId)) {
            watchList.add(itemId);
        }

        discordUser.setWatchList(watchList);
        updateOneUser(discordUser);
    }

    /**
     * Gets watch list by user id.
     *
     * @param currentUserId the current user id
     * @return the watch list by user id
     */
    public List<AuctionItem> getWatchListByUserId(String currentUserId) {
        DiscordUser discordUser = getDiscordUserByUserId(currentUserId);
        List<ObjectId> watchList = discordUser.getWatchList();
        if (watchList == null) {
            return new ArrayList<>();
        }

        List<AuctionItem> auctionItems = new ArrayList<>();
        for (ObjectId itemId : watchList) {
            AuctionItem auctionItem = itemRepository.get(itemId);
            if (auctionItem != null && auctionItem.getAuctionStatus().equals("IN_PROGRESS")) {
                auctionItems.add(auctionItem);
            }
        }
        return auctionItems;
    }

    /**
     * Gets discord user by user id.
     *
     * @param discordUserId the discord user id
     * @return the discord user by user id
     */
    public DiscordUser getDiscordUserByUserId(String discordUserId) {
        Collection<DiscordUser> discordUsers = discordUserRepository.getAll();
        for (DiscordUser currentDiscordUser : discordUsers) {
            if (currentDiscordUser.getDiscordUserId().equals(discordUserId)) {
                return currentDiscordUser;
            }
        }

        DiscordUser discordUser = new DiscordUser();
        discordUser.setDiscordUserId(discordUserId);
        discordUserRepository.add(discordUser);
        return discordUser;
    }
}
