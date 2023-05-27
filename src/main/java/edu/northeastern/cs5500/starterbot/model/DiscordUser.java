package edu.northeastern.cs5500.starterbot.model;

import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class DiscordUser implements Model {
    ObjectId id;

    String discordUserId;

    String preferredName;
    String username;
    String discriminator;
    String avatarUrl;
    List<ObjectId> sellerInventory;
    List<ObjectId> watchList;
}
