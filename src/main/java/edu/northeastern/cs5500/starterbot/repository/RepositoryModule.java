package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.AuctionItem;
import edu.northeastern.cs5500.starterbot.model.DiscordUser;

@Module
public class RepositoryModule {

    @Provides
    public GenericRepository<AuctionItem> provideAuctionItemRepository(
            MongoDBRepository<AuctionItem> repository) {
        return repository;
    }

    @Provides
    public Class<AuctionItem> provideAuctionItem() {
        return AuctionItem.class;
    }

    @Provides
    public GenericRepository<DiscordUser> provideDiscordUserRepository(
            MongoDBRepository<DiscordUser> repository) {
        return repository;
    }

    @Provides
    public Class<DiscordUser> provideDiscordUser() {
        return DiscordUser.class;
    }
}
