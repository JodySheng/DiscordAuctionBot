package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.Paginator;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.Test;

public class PaginatorControllerTest {

    @Inject JDA jda;

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

    Paginator CreatePaginator(List<AuctionItem> auctionItems, int itemsPerPage) {
        return new Paginator(auctionItems, itemsPerPage);
    }

    PaginatorController getPaginatorController() {
        return new PaginatorController();
    }

    public AuctionController getAuctionController() {
        AuctionController auctionController =
                new AuctionController(new InMemoryRepository<AuctionItem>());
        return auctionController;
    }

    @Test
    public void getTotalPagesTest() {
        PaginatorController paginatorController = getPaginatorController();
        AuctionController auctionController = new AuctionController(new InMemoryRepository<>());
        AuctionItem auctionItem1 =
                auctionController.createAuctionItem(
                        itemName1,
                        description1,
                        condition1,
                        startPrice1,
                        minBid1,
                        duration1,
                        imgUrl1,
                        sellerId1);
        List<AuctionItem> auctionItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            auctionItems.add(auctionItem1);
        }
        Paginator paginator = CreatePaginator(auctionItems, 5);
        assertThat(paginator.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void trimPaginatorsTest() {
        PaginatorController paginatorController = getPaginatorController();
        List<AuctionItem> auctionItems = new ArrayList<>();
        Paginator paginator = paginatorController.createPaginator(auctionItems, 3);
        String originalPaginatorId = paginator.getId().toString();
        for (int i = 0; i < 10; i++) {
            paginatorController.createPaginator(auctionItems, 3);
        }
        paginatorController.trimPaginators();
        assertThat(paginatorController.getPaginator(originalPaginatorId)).isEqualTo(null);
        ;
    }

    @Test
    public void createPaginatorTest() {
        PaginatorController paginatorController = getPaginatorController();
        AuctionController auctionController = new AuctionController(new InMemoryRepository<>());
        AuctionItem auctionItem1 =
                auctionController.createAuctionItem(
                        itemName1,
                        description1,
                        condition1,
                        startPrice1,
                        minBid1,
                        duration1,
                        imgUrl1,
                        sellerId1);
        List<AuctionItem> auctionItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            auctionItems.add(auctionItem1);
        }
        Paginator paginator = paginatorController.createPaginator(auctionItems, 5);
        assertThat(paginator.getTotalPages()).isEqualTo(2);
    }
}
