package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class AuctionControllerTest {

    AuctionController auctionController;
    AuctionItem auctionItem1;

    @Nonnull String itemName1 = "testItem";
    @Nonnull String description1 = "testDescription";
    @Nonnull String condition1 = "testCondition";
    @Nonnull Double startPrice1 = 300.0;
    @Nonnull Double minBid1 = 300.0;
    @Nonnull Integer duration1 = 300;
    @Nonnull String imgUrl1 = "https://i.imgur.com/S0uezbH.png";
    @Nonnull String sellerId1 = "978312461277093938";
    @Nonnull String auctionStatus = "IN_PROGRESS";
    @Nonnull String discordMessageUrl = "http://testUrl";
    @Nonnull String publicMessageId = "1411341463523";
    @Nonnull String publicChannelId = "3546346356345";
    @Nonnull String sellerChannelId = "1124634634566";
    @Nonnull String sellerChannelMessageId = "1223746856734";
    @Nonnull LocalDateTime auctionStartTime1 = LocalDateTime.now();
    @Nonnull LocalDateTime auctionEndTime1 = LocalDateTime.now().plusDays(1l);

    public AuctionController getAuctionController() {
        auctionController = new AuctionController(new InMemoryRepository<AuctionItem>());
        return auctionController;
    }

    public AuctionItem makeAuctionItem(
            @Nonnull String itemName,
            @Nonnull String auctionStatus,
            @Nonnull String sellerId,
            @Nonnull String discordMessageUrl,
            @Nonnull String publicMessageId,
            @Nonnull String publicChannelId,
            @Nonnull String sellerChannelId,
            @Nonnull String sellerChannelMessageId) {
        return AuctionItem.builder()
                .itemName(itemName)
                .auctionStatus(auctionStatus)
                .description(description1)
                .condition(condition1)
                .imgUrl(imgUrl1)
                .startPrice(startPrice1)
                .minBid(minBid1)
                .duration(duration1)
                .auctionStartTime(auctionStartTime1)
                .auctionEndTime(auctionEndTime1)
                .sellerId(sellerId)
                .discordMessageUrl(discordMessageUrl)
                .publicMessageId(publicMessageId)
                .publicChannelId(publicChannelId)
                .sellerChannelId(sellerChannelId)
                .sellerChannelMessageId(sellerChannelMessageId)
                .build();
    }

    @Test
    public void testAddOneItem() {
        // setup
        auctionController = getAuctionController();
        auctionItem1 =
                makeAuctionItem(
                        itemName1,
                        auctionStatus,
                        sellerId1,
                        discordMessageUrl,
                        publicMessageId,
                        publicChannelId,
                        sellerChannelId,
                        sellerChannelMessageId);
        AuctionItem testAuctionItem = auctionController.addAuctionItem(auctionItem1);

        assertThat(testAuctionItem.getItemName()).isEqualTo(itemName1);
        assertThat(testAuctionItem.getDescription()).isEqualTo(description1);
        assertThat(testAuctionItem.getCondition()).isEqualTo(condition1);
        assertThat(testAuctionItem.getStartPrice()).isEqualTo(startPrice1);
        assertThat(testAuctionItem.getDuration()).isEqualTo(duration1);
        assertThat(testAuctionItem.getImgUrl()).isEqualTo(imgUrl1);
        assertThat(testAuctionItem.getSellerId()).isEqualTo(sellerId1);
    }

    @Test
    public void testEdit() {
        AuctionItem testAuctionItem = makeItem();
        auctionController.updateItemUsingEdit(
                testAuctionItem.getId(), "name", description1, discordMessageUrl);
        assertThat(testAuctionItem.getItemName()).isEqualTo("name");
    }

    private AuctionItem makeItem() {
        auctionController = getAuctionController();
        auctionItem1 =
                makeAuctionItem(
                        itemName1,
                        auctionStatus,
                        sellerId1,
                        discordMessageUrl,
                        publicMessageId,
                        publicChannelId,
                        sellerChannelId,
                        sellerChannelMessageId);
        return auctionController.addAuctionItem(auctionItem1);
    }

    @Test
    public void testGetItem() {
        AuctionItem testAuctionItem = makeItem();
        ObjectId testId = testAuctionItem.getId();
        assertThat(auctionController.getAuctionItem(testId));
        testAuctionItem.setItemName("TEST");
        auctionController.updateOneItem(testAuctionItem);
        assertThat(auctionController.getAuctionItem(testId).getItemName()).isEqualTo("TEST");
    }

    @Test
    public void testUpdateOneItemById() {
        AuctionController auctionController = getAuctionController();
        AuctionItem testAuctionItem =
                auctionController.createAuctionItem(
                        itemName1,
                        description1,
                        "TEST_CONDITION",
                        500d,
                        minBid1,
                        duration1,
                        imgUrl1,
                        sellerId1);
        ObjectId testId = testAuctionItem.getId();
        auctionController.updateOneItemByID(testId, "MAC", "TEST_DESCRIPTION", 500d, "NEW", 600);
        assertThat(auctionController.getAuctionItem(testId).getItemName()).isEqualTo("MAC");
    }

    @Test
    public void testCreateItem() {
        AuctionItem testItem = makeItem();
        assertThat(auctionController.getObjectID(testItem).equals(testItem.getId()));
    }

    @Test
    public void testUpdate() {
        AuctionItem testItem = makeItem();
        auctionController.updateItemDuration(testItem.getId(), 2);
        assertThat(auctionController.getItemById(testItem.getId()).getDuration().equals(2));
        auctionController.updateCurrentBidByID(testItem.getId(), 100.0, "111");
        assertThat(auctionController.getItemById(testItem.getId()).getCurrentBid().equals(100.0));
    }

    @Test
    public void testRemove() {
        AuctionItem testItem = makeItem();
        auctionController.removeItemById(testItem.getId());
        assertThat(auctionController.getItemById(testItem.getId()).equals(null));
    }

    @Test
    public void testGetSearchList() {
        AuctionController auctionController = getAuctionController();

        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                itemName1,
                description1,
                "TEST_CONDITION",
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                itemName1,
                description1,
                "TEST_CONDITION",
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                itemName1,
                description1,
                "TEST_CONDITION",
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                itemName1, description1, condition1, 500d, minBid1, duration1, imgUrl1, sellerId1);
        auctionController.createAuctionItem(
                itemName1, description1, condition1, 500d, minBid1, duration1, imgUrl1, sellerId1);
        AuctionItem auctionItem1 =
                auctionController.createAuctionItem(
                        "ipad",
                        description1,
                        condition1,
                        500d,
                        minBid1,
                        duration1,
                        imgUrl1,
                        sellerId1);
        auctionItem1.setCurrentBid(100d);
        auctionController.updateOneItem(auctionItem1);

        assertThat(
                        auctionController
                                .getSearchList("TEST_NAME", null, null, null)
                                .get(0)
                                .getItemName())
                .isEqualTo("TEST_NAME");
        assertThat(auctionController.getSearchList("TEST_NAME", "NEW", 100d, 500d).size())
                .isEqualTo(0);
        assertThat(
                        auctionController
                                .getSearchList("", "TEST_CONDITION", null, null)
                                .get(0)
                                .getCondition())
                .isEqualTo("TEST_CONDITION");
        assertThat(auctionController.getSearchList(null, null, 10d, 300d).get(0).getItemName())
                .isEqualTo("ipad");
        assertThat(auctionController.getSearchList(null, null, null, null).size()).isEqualTo(7);
    }

    @Test
    public void testGetAllItems() {
        AuctionController auctionController = getAuctionController();
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        assertThat(auctionController.getAllItems().size()).isEqualTo(1);
    }

    @Test
    public void testGetItemBySellerUserId() {
        AuctionController auctionController = getAuctionController();
        auctionController.createAuctionItem(
                "TEST_NAME1",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                "TEST_NAME2",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                "214324123412");
        assertThat(auctionController.getItemBySellerUserId(sellerId1).get(0).getSellerId())
                .isEqualTo(sellerId1);
    }

    @Test
    public void testIsSeller() {
        AuctionController auctionController = getAuctionController();
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        assertThat(
                        auctionController.isSeller(
                                (auctionController.getItemBySellerUserId(sellerId1).get(0).getId()),
                                sellerId1))
                .isEqualTo(true);

        assertThat(
                        auctionController.isSeller(
                                (auctionController.getItemBySellerUserId(sellerId1).get(0).getId()),
                                "2352313"))
                .isEqualTo(false);
    }

    @Test
    public void testIsCurrentBuyer() {
        AuctionController auctionController = getAuctionController();
        AuctionItem auctionItem =
                auctionController.createAuctionItem(
                        "TEST_NAME",
                        description1,
                        condition1,
                        500d,
                        minBid1,
                        duration1,
                        imgUrl1,
                        sellerId1);
        String currentBuyer = "123123123";
        auctionItem.setCurrentBidUserId(currentBuyer);
        assertThat(auctionController.isCurrentBuyer(auctionItem.getId(), currentBuyer))
                .isEqualTo(true);
        assertThat(auctionController.isCurrentBuyer(auctionItem.getId(), "123123123123"))
                .isEqualTo(false);
    }

    @Test
    public void testGetRecentList() {
        AuctionController auctionController = getAuctionController();
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        assertThat(auctionController.getRecentList().size()).isEqualTo(3);
    }

    @Test
    public void testFinishAuction() {
        AuctionController auctionController = getAuctionController();
        AuctionItem auctionItem =
                auctionController.createAuctionItem(
                        "TEST_NAME",
                        description1,
                        condition1,
                        startPrice1,
                        minBid1,
                        duration1,
                        imgUrl1,
                        sellerId1);
        auctionController.finishAuction(auctionItem);
        assertThat(auctionController.getItemById(auctionItem.getId()).getAuctionStatus())
                .isEqualTo("EXPIRED");
    }

    @Test
    public void testGetSellList() {
        AuctionController auctionController = getAuctionController();
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                sellerId1);
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                "1231341421431");
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                "2414124123412");
        auctionController.createAuctionItem(
                "TEST_NAME",
                description1,
                condition1,
                500d,
                minBid1,
                duration1,
                imgUrl1,
                "3214324123412");
        assertThat(auctionController.getSellList(sellerId1).size()).isEqualTo(3);
    }
}
