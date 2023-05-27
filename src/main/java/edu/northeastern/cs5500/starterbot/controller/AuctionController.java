package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.bson.types.ObjectId;

@Slf4j
public class AuctionController {

    static GenericRepository<AuctionItem> itemRepository;

    @Inject JDA jda;

    /**
     * Instantiates a new Auction controller.
     *
     * @param itemRepository the item repository
     */
    @Inject
    AuctionController(GenericRepository<AuctionItem> itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Create auction item auction item.
     *
     * @param itemName the item name
     * @param description the description
     * @param condition the condition
     * @param startPrice the start price
     * @param minBid the min bid
     * @param duration the duration
     * @param imgUrl the img url
     * @param sellerId the seller id
     * @return the auction item
     */
    public AuctionItem createAuctionItem(
            @Nonnull String itemName,
            @Nonnull String description,
            @Nonnull String condition,
            @Nonnull Double startPrice,
            @Nonnull Double minBid,
            @Nonnull Integer duration,
            @Nonnull String imgUrl,
            @Nonnull String sellerId) {

        LocalDateTime curTime = LocalDateTime.now().minusHours(7);

        AuctionItem item =
                AuctionItem.builder()
                        .itemName(itemName)
                        .description(description)
                        .condition(condition)
                        .startPrice(startPrice)
                        .minBid(minBid)
                        .currentBid(startPrice)
                        .duration(duration)
                        .auctionStartTime(curTime)
                        .auctionEndTime(curTime.plusSeconds(duration))
                        .imgUrl(imgUrl)
                        .sellerId(sellerId)
                        .auctionStatus("IN_PROGRESS")
                        .currentBidUserId(null)
                        .build();

        AuctionItem newItem = itemRepository.add(item);
        return newItem;
    }

    /**
     * Add auction item auction item.
     *
     * @param item the item
     * @return the auction item
     */
    public AuctionItem addAuctionItem(AuctionItem item) {
        return itemRepository.add(item);
    }

    /**
     * Gets object id.
     *
     * @param item the item
     * @return the object id
     */
    public ObjectId getObjectID(AuctionItem item) {
        return item.getId();
    }

    /**
     * Gets auction item.
     *
     * @param id the id
     * @return the auction item
     */
    public static AuctionItem getAuctionItem(ObjectId id) {
        return itemRepository.get(id);
    }

    /**
     * Update one item auction item.
     *
     * @param item the item
     * @return the auction item
     */
    public AuctionItem updateOneItem(AuctionItem item) {
        return itemRepository.update(item);
    }

    /**
     * Update one item by id auction item.
     *
     * @param itemID the item id
     * @param name the name
     * @param description the description
     * @param price the price
     * @param condition the condition
     * @param duration the duration
     * @return the auction item
     */
    public AuctionItem updateOneItemByID(
            ObjectId itemID,
            String name,
            String description,
            Double price,
            String condition,
            Integer duration) {
        AuctionItem newItem = itemRepository.get(itemID);
        newItem.setItemName(name);
        newItem.setDescription(description);
        newItem.setStartPrice(price);
        newItem.setCondition(condition);
        newItem.setDuration(duration);
        LocalDateTime curTime = itemRepository.get(itemID).getAuctionStartTime();
        newItem.setAuctionEndTime(curTime.plusDays(duration));

        return itemRepository.update(newItem);
    }

    /**
     * Update item using edit auction item.
     *
     * @param itemID the item id
     * @param name the name
     * @param description the description
     * @param url the url
     * @return the auction item
     */
    public AuctionItem updateItemUsingEdit(
            ObjectId itemID, String name, String description, String url) {
        AuctionItem newItem = itemRepository.get(itemID);
        newItem.setItemName(name);
        newItem.setDescription(description);
        newItem.setImgUrl(url);
        itemRepository.update(newItem);
        return newItem;
    }

    /**
     * Update item duration auction item.
     *
     * @param itemID the item id
     * @param duration the duration
     * @return the auction item
     */
    public AuctionItem updateItemDuration(ObjectId itemID, Integer duration) {
        AuctionItem newItem = itemRepository.get(itemID);
        LocalDateTime curEndTime = newItem.getAuctionEndTime();
        newItem.setAuctionEndTime(curEndTime.plusDays(duration));
        itemRepository.update(newItem);
        return newItem;
    }

    /**
     * Update current bid by id auction item.
     *
     * @param itemID the item id
     * @param price the price
     * @param currentBidUserId the current bid user id
     * @return the auction item
     */
    public AuctionItem updateCurrentBidByID(
            ObjectId itemID, Double price, String currentBidUserId) {
        AuctionItem newItem = itemRepository.get(itemID);
        newItem.setCurrentBid(price);
        newItem.setCurrentBidUserId(currentBidUserId);
        itemRepository.update(newItem);
        return newItem;
    }

    /**
     * Gets item by id.
     *
     * @param id the id
     * @return the item by id
     */
    public AuctionItem getItemById(ObjectId id) {
        return itemRepository.get(id);
    }

    /**
     * Gets item by seller user id.
     *
     * @param userId the user id
     * @return the item by seller user id
     */
    public List<AuctionItem> getItemBySellerUserId(String userId) {
        Collection<AuctionItem> items = itemRepository.getAll();
        return items.stream().filter(item -> item.getSellerId().equals(userId)).toList();
    }

    /**
     * Remove item by id.
     *
     * @param itemId the item id
     */
    public void removeItemById(ObjectId itemId) {
        log.info("begin to delete item by id : " + itemId.toString());
        AuctionItem auctionItem = itemRepository.get(itemId);
        auctionItem.setAuctionStatus("WITHDRAW");
        itemRepository.update(auctionItem);
        log.info(
                "successfully delete item by id : "
                        + itemId.toString()
                        + ",after that, its status is "
                        + auctionItem.getAuctionStatus());
    }

    /**
     * Is seller boolean.
     *
     * @param itemId the item id
     * @param userId the user id
     * @return the boolean
     */
    public boolean isSeller(ObjectId itemId, String userId) {
        String itemSeller = itemRepository.get(itemId).getSellerId();
        if (userId.equals(itemSeller)) {
            return true;
        }
        return false;
    }

    /**
     * Is current buyer boolean.
     *
     * @param itemId the item id
     * @param userId the user id
     * @return the boolean
     */
    public boolean isCurrentBuyer(ObjectId itemId, String userId) {
        String itemSeller = itemRepository.get(itemId).getCurrentBidUserId();
        if (userId.equals(itemSeller)) {
            return true;
        }
        return false;
    }

    /**
     * Gets all items.
     *
     * @return the all items
     */
    @Nullable
    public List<AuctionItem> getAllItems() {
        Collection<AuctionItem> items = itemRepository.getAll();
        return items.stream().toList();
    }

    /**
     * Finish auction auction item.
     *
     * @param auctionItem the auction item
     * @return the auction item
     */
    public AuctionItem finishAuction(AuctionItem auctionItem) {
        AuctionItem finishedItem = itemRepository.get(auctionItem.getId());
        finishedItem.setAuctionStatus("EXPIRED");
        itemRepository.update(finishedItem);
        return finishedItem;
    }

    /**
     * Gets sell list.
     *
     * @param currentUserId the current user id
     * @return the sell list
     */
    public List<AuctionItem> getSellList(String currentUserId) {
        Collection<AuctionItem> items = itemRepository.getAll();
        return items.stream()
                .filter(
                        item ->
                                item.getAuctionEndTime()
                                                .isAfter(
                                                        java.time.LocalDateTime.now().minusHours(7))
                                        && currentUserId.equals(item.getSellerId()))
                .sorted(Comparator.comparing(AuctionItem::getAuctionStartTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Gets recent list.
     *
     * @return the recent list
     */
    public List<AuctionItem> getRecentList() {
        Collection<AuctionItem> items = itemRepository.getAll();
        return items.stream()
                .filter(
                        item ->
                                item.getAuctionEndTime()
                                                .isAfter(
                                                        java.time.LocalDateTime.now().minusHours(7))
                                        && item.getAuctionStatus().equals("IN_PROGRESS"))
                .sorted(Comparator.comparing(AuctionItem::getAuctionStartTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Gets search list.
     *
     * @param itemName the item name
     * @param condition the condition
     * @param startPrice the start price
     * @param endPrice the end price
     * @return the search list
     */
    public List<AuctionItem> getSearchList(
            String itemName, String condition, Double startPrice, Double endPrice) {
        Collection<AuctionItem> items = itemRepository.getAll();
        return items.stream()
                .filter(
                        item ->
                                item.getAuctionEndTime()
                                                .isAfter(
                                                        java.time.LocalDateTime.now().minusHours(7))
                                        && (item.getAuctionStatus().equals("IN_PROGRESS"))
                                        && (itemName == null
                                                || item.getItemName()
                                                        .toLowerCase()
                                                        .contains(itemName.toLowerCase()))
                                        && (condition == null
                                                || item.getCondition().equals(condition))
                                        && (startPrice == null
                                                || item.getCurrentBid() >= startPrice)
                                        && (endPrice == null || item.getCurrentBid() <= endPrice))
                .sorted(Comparator.comparing(AuctionItem::getAuctionStartTime).reversed())
                .collect(Collectors.toList());
    }
}
