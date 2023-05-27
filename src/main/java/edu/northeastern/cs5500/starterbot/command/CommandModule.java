package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public class CommandModule {

    /**
     * Provide aution add command slash command handler.
     *
     * @param auctionAddCommand the auction add command
     * @return the slash command handler
     */
    @Provides
    @IntoSet
    public SlashCommandHandler provideAutionAddCommand(AuctionAddCommand auctionAddCommand) {
        return auctionAddCommand;
    }

    /**
     * Provide auction add command click handler button handler.
     *
     * @param auctionEditButtonHandler the auction edit button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionAddCommandClickHandler(
            AuctionEditButtonHandler auctionEditButtonHandler) {
        return auctionEditButtonHandler;
    }

    /**
     * Provide auction withdraw command click handler button handler.
     *
     * @param auctionWithdrawButtonHandler the auction withdraw button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionWithdrawCommandClickHandler(
            AuctionWithdrawButtonHandler auctionWithdrawButtonHandler) {
        return auctionWithdrawButtonHandler;
    }

    /**
     * Provide auction withdraw cancel command click handler button handler.
     *
     * @param auctionWithdrawCancelButtonHandler the auction withdraw cancel button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionWithdrawCancelCommandClickHandler(
            AuctionWithdrawCancelButtonHandler auctionWithdrawCancelButtonHandler) {
        return auctionWithdrawCancelButtonHandler;
    }

    /**
     * Provide auction withdraw confirm command click handler button handler.
     *
     * @param auctionWithdrawConfirmButtonHandler the auction withdraw confirm button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionWithdrawConfirmCommandClickHandler(
            AuctionWithdrawConfirmButtonHandler auctionWithdrawConfirmButtonHandler) {
        return auctionWithdrawConfirmButtonHandler;
    }

    /**
     * Provide auction bid command click handler button handler.
     *
     * @param auctionBidButtonHandler the auction bid button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionBidCommandClickHandler(
            AuctionBidButtonHandler auctionBidButtonHandler) {
        return auctionBidButtonHandler;
    }

    /**
     * Provide auction min bid command click handler button handler.
     *
     * @param auctionMinBidButtonHandler the auction min bid button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionMinBidCommandClickHandler(
            AuctionMinBidButtonHandler auctionMinBidButtonHandler) {
        return auctionMinBidButtonHandler;
    }

    /**
     * Provide auction min bid cancel command click handler button handler.
     *
     * @param auctionMinBidCancelButtonHandler the auction min bid cancel button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionMinBidCancelCommandClickHandler(
            AuctionMinBidCancelButtonHandler auctionMinBidCancelButtonHandler) {
        return auctionMinBidCancelButtonHandler;
    }

    /**
     * Provide auction min bid conirm command click handler button handler.
     *
     * @param auctionMinBidConirmButtonHandler the auction min bid conirm button handler
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideAuctionMinBidConirmCommandClickHandler(
            AuctionMinBidConfirmButtonHandler auctionMinBidConirmButtonHandler) {
        return auctionMinBidConirmButtonHandler;
    }

    /**
     * Provide recent list command handler slash command handler.
     *
     * @param recentlistCommand the recentlist command
     * @return the slash command handler
     */
    @Provides
    @IntoSet
    public SlashCommandHandler provideRecentListCommandHandler(
            RecentListCommand recentlistCommand) {
        return recentlistCommand;
    }

    /**
     * Provide recent list command clicker handler button handler.
     *
     * @param recentlistCommand the recentlist command
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideRecentListCommandClickerHandler(
            RecentListCommand recentlistCommand) {
        return recentlistCommand;
    }

    /**
     * Provide search command handler slash command handler.
     *
     * @param searchCommand the search command
     * @return the slash command handler
     */
    @Provides
    @IntoSet
    public SlashCommandHandler provideSearchCommandHandler(SearchCommand searchCommand) {
        return searchCommand;
    }

    /**
     * Provide search command clicker handler button handler.
     *
     * @param searchCommand the search command
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideSearchCommandClickerHandler(SearchCommand searchCommand) {
        return searchCommand;
    }

    /**
     * Provide help command handler slash command handler.
     *
     * @param helpCommand the help command
     * @return the slash command handler
     */
    @Provides
    @IntoSet
    public SlashCommandHandler provideHelpCommandHandler(HelpCommand helpCommand) {
        return helpCommand;
    }

    /**
     * Provide sell list command handler slash command handler.
     *
     * @param sellListCommand the sell list command
     * @return the slash command handler
     */
    @Provides
    @IntoSet
    public SlashCommandHandler provideSellListCommandHandler(SellListCommand sellListCommand) {
        return sellListCommand;
    }

    /**
     * Provide sell list command clicker handler button handler.
     *
     * @param sellListCommand the sell list command
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideSellListCommandClickerHandler(SellListCommand sellListCommand) {
        return sellListCommand;
    }

    /**
     * Provide watch list command handler slash command handler.
     *
     * @param watchListCommand the watch list command
     * @return the slash command handler
     */
    @Provides
    @IntoSet
    public SlashCommandHandler provideWatchListCommandHandler(WatchListCommand watchListCommand) {
        return watchListCommand;
    }

    /**
     * Provide watch list command clicker handler button handler.
     *
     * @param watchListCommand the watch list command
     * @return the button handler
     */
    @Provides
    @IntoSet
    public ButtonHandler provideWatchListCommandClickerHandler(WatchListCommand watchListCommand) {
        return watchListCommand;
    }

    /**
     * Provide auction edit modal handler modal handler.
     *
     * @param auctionEditModalHandler the auction edit modal handler
     * @return the modal handler
     */
    @Provides
    @IntoSet
    public ModalHandler provideAuctionEditModalHandler(
            AuctionEditModalHandler auctionEditModalHandler) {
        return auctionEditModalHandler;
    }

    /**
     * Provide auction bid modal handler modal handler.
     *
     * @param auctionBidModalHandler the auction bid modal handler
     * @return the modal handler
     */
    @Provides
    @IntoSet
    public ModalHandler provideAuctionBidModalHandler(
            AuctionBidModalHandler auctionBidModalHandler) {
        return auctionBidModalHandler;
    }
}
