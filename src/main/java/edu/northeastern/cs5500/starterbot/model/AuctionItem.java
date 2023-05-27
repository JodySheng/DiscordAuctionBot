package edu.northeastern.cs5500.starterbot.model;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionItem implements Model {

    ObjectId id;
    @Nonnull String itemName;
    @Nonnull String description;
    @Nonnull String condition;
    @Nonnull String imgUrl;
    @Nonnull Double startPrice;
    @Nonnull Double minBid;
    @Nullable Double currentBid;
    @Nonnull Integer duration;
    @Nonnull LocalDateTime auctionStartTime;
    @Nonnull LocalDateTime auctionEndTime;
    @Nonnull String sellerId;
    @Nullable String currentBidUserId;
    @Nullable String discordMessageUrl;
    @Nonnull String auctionStatus;

    @Nullable String publicMessageId;
    @Nullable String publicChannelId;

    @Nullable String sellerChannelId;
    @Nullable String sellerChannelMessageId;
}
