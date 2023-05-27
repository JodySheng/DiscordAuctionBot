package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.DiscordUser;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class UserControllerTest {

    DiscordUser user1;
    String userName1 = "Jojo";
    String preferredName1 = "Jojo";
    String discordUserId1 = "1";

    ObjectId objectId1 = new ObjectId();
    ObjectId objectId2 = new ObjectId();
    ObjectId objectId3 = new ObjectId();
    ObjectId objectId4 = new ObjectId();
    ObjectId objectId5 = new ObjectId();
    final AuctionItem auctionItem4;
    final AuctionItem auctionItem5;

    List<AuctionItem> items;

    AuctionItem makeAuctionItem(
            ObjectId id, @Nonnull String itemName, @Nonnull String auctionStatus) {
        return AuctionItem.builder()
                .id(id)
                .itemName(itemName)
                .auctionStatus(auctionStatus)
                .description("")
                .condition("")
                .imgUrl("")
                .startPrice(0.)
                .minBid(0.)
                .duration(0)
                .auctionStartTime(LocalDateTime.now())
                .auctionEndTime(LocalDateTime.now())
                .sellerId("")
                .discordMessageUrl("")
                .publicMessageId("")
                .publicChannelId("")
                .sellerChannelId("")
                .sellerChannelMessageId("")
                .build();
    }

    public UserControllerTest() {
        user1 = new DiscordUser();
        user1.setDiscordUserId(discordUserId1);
        user1.setUsername(userName1);
        user1.setPreferredName(preferredName1);
        List<ObjectId> watchList = new ArrayList<ObjectId>();

        watchList.add(objectId1);
        watchList.add(objectId2);
        watchList.add(objectId3);
        user1.setWatchList(watchList);

        AuctionItem auctionItem1 = makeAuctionItem(objectId1, "item1", "IN_PROGRESS");
        AuctionItem auctionItem2 = makeAuctionItem(objectId2, "item2", "Finished");
        AuctionItem auctionItem3 = makeAuctionItem(objectId3, "item3", "IN_PROGRESS");

        auctionItem4 = makeAuctionItem(objectId4, "item4", "IN_PROGRESS");

        auctionItem5 = makeAuctionItem(objectId5, "item5", "SOLD");

        items = new ArrayList<>();
        items.add(auctionItem1);
        items.add(auctionItem2);
        items.add(auctionItem3);
    }

    private UserController getUserController() {
        GenericRepository<DiscordUser> inMemoryDiscordUserRepository =
                new InMemoryRepository<DiscordUser>();
        GenericRepository<AuctionItem> inMemoryAuctionItemRepository =
                new InMemoryRepository<AuctionItem>();
        inMemoryDiscordUserRepository.add(user1);
        for (var item : items) {
            inMemoryAuctionItemRepository.add(item);
        }
        return new UserController(inMemoryDiscordUserRepository, inMemoryAuctionItemRepository);
    }

    @Test
    void testGetDiscordUserByUserId() {
        UserController userController = getUserController();
        DiscordUser discordUser = userController.getDiscordUserByUserId(discordUserId1);
        assertThat(discordUser.getUsername()).isEqualTo(userName1);
    }

    @Test
    void testGetWatchListByUserId() {
        UserController userController = getUserController();
        List<AuctionItem> auctionItems = userController.getWatchListByUserId(discordUserId1);
        List<ObjectId> itemIds =
                auctionItems.stream().map(AuctionItem::getId).collect(Collectors.toList());
        List<ObjectId> expectedIds = new ArrayList<>(Arrays.asList(objectId1, objectId3));
        assertThat(itemIds).isEqualTo(expectedIds);
    }

    @Test
    void testAddItemToWatchList() {
        UserController userController = getUserController();
        List<ObjectId> itemIds =
                userController.getDiscordUserByUserId(discordUserId1).getWatchList();
        List<ObjectId> beforeAddedIds =
                new ArrayList<>(Arrays.asList(objectId1, objectId2, objectId3));
        assertThat(itemIds).isEqualTo(beforeAddedIds);
        userController.addItemToWatchList(discordUserId1, objectId4);
        itemIds = userController.getDiscordUserByUserId(discordUserId1).getWatchList();
        List<ObjectId> expectedIds =
                new ArrayList<>(Arrays.asList(objectId1, objectId2, objectId3, objectId4));
        assertThat(itemIds).isEqualTo(expectedIds);
    }

    @Test
    void testAddItemToNonNullWatchList() {
        UserController userController = getUserController();
        List<ObjectId> itemIds =
                userController.getDiscordUserByUserId(discordUserId1).getWatchList();
        List<ObjectId> beforeAddedIds =
                new ArrayList<>(Arrays.asList(objectId1, objectId2, objectId3));
        assertThat(itemIds).isEqualTo(beforeAddedIds);
        userController.addItemToWatchList(discordUserId1, objectId4);
        itemIds = userController.getDiscordUserByUserId(discordUserId1).getWatchList();
        List<ObjectId> expectedIds =
                new ArrayList<>(Arrays.asList(objectId1, objectId2, objectId3, objectId4));
        assertThat(itemIds).isEqualTo(expectedIds);

        userController.addItemToWatchList(discordUserId1, objectId4);
        itemIds = userController.getDiscordUserByUserId(discordUserId1).getWatchList();
        List<ObjectId> itemIdList1 =
                userController.getDiscordUserByUserId("testUserId").getWatchList();
        assertThat(itemIdList1).isEqualTo(null);
        assertThat(itemIds.size()).isEqualTo(4);
    }

    @Test
    void testAddItemToSellerInventory() {
        UserController userController = getUserController();
        List<ObjectId> itemIds =
                userController.getDiscordUserByUserId(discordUserId1).getSellerInventory();
        assertThat(itemIds).isEqualTo(null);
        userController.addItemToSellerInventory(user1, auctionItem5);
        itemIds = userController.getDiscordUserByUserId(discordUserId1).getSellerInventory();
        List<ObjectId> expectedIds = new ArrayList<>(Arrays.asList(objectId5));
        assertThat(itemIds).isEqualTo(expectedIds);
    }

    @Test
    void testAddItemToNonNullSellerInventory() {
        UserController userController = getUserController();
        List<ObjectId> itemIds =
                userController.getDiscordUserByUserId(discordUserId1).getSellerInventory();
        assertThat(itemIds).isEqualTo(null);
        userController.addItemToSellerInventory(user1, auctionItem5);
        itemIds = userController.getDiscordUserByUserId(discordUserId1).getSellerInventory();
        List<ObjectId> expectedIds = new ArrayList<>(Arrays.asList(objectId5));
        assertThat(itemIds).isEqualTo(expectedIds);

        userController.addItemToSellerInventory(user1, auctionItem5);
        itemIds = userController.getDiscordUserByUserId(discordUserId1).getSellerInventory();
        assertThat(itemIds.size()).isEqualTo(2);
    }

    @Test
    void testUpdateOneUser() {
        UserController userController = getUserController();
        user1.setPreferredName("jennie");
        userController.updateOneUser(user1);
        DiscordUser user = userController.getDiscordUserByUserId(user1.getDiscordUserId());
        assertThat(user.getPreferredName()).isEqualTo("jennie");
    }
}
