package me.dodo.readingnotes.dto.book;

import me.dodo.readingnotes.dto.reading.ReadingRecordItem;

import java.util.List;

public class BookRecordsPageResponse  {
    private BookMetaResponse book;
    private List<ReadingRecordItem> content;
    private String nextCursor;
    private boolean hasMore;

    public BookRecordsPageResponse(BookMetaResponse book, List<ReadingRecordItem> content, String nextCursor, boolean hasMore) {
        this.book = book;
        this.content = content;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    // Getter
    public BookMetaResponse getBook() { return book; }
    public List<ReadingRecordItem> getContent() { return content; }
    public String getNextCursor() { return nextCursor; }
    public boolean getHasMore() { return hasMore; }
}
