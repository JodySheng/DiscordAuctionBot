package edu.northeastern.cs5500.starterbot.model;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@Data
@RequiredArgsConstructor
public class Paginator implements Model {
    ObjectId id;
    @Nonnull @Getter List<AuctionItem> data;
    @Getter final int itemsPerPage;
    int currentPage = 0;

    /**
     * Gets current page data.
     *
     * @return the current page data
     */
    public List<AuctionItem> getCurrentPageData() {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, data.size());
        return data.subList(startIndex, endIndex);
    }

    /**
     * Gets total pages.
     *
     * @return the total pages
     */
    public int getTotalPages() {
        return (data.size() / itemsPerPage) + (data.size() % itemsPerPage > 0 ? 1 : 0);
    }
}
