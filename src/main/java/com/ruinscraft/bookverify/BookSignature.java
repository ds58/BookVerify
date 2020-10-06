package com.ruinscraft.bookverify;

import com.google.gson.Gson;
import org.bukkit.inventory.meta.BookMeta;

public class BookSignature {

    private static final Gson GSON = new Gson();

    private String author;
    private String contentHash;
    private long signTime;

    public BookSignature(BookMeta bookMeta) {
        author = bookMeta.getAuthor();
        contentHash = BookSignatureUtil.getContentHash(bookMeta);
        signTime = System.currentTimeMillis();
    }

    public BookSignature(String author, String contentHash, long signTime) {
        this.author = author;
        this.contentHash = contentHash;
        this.signTime = signTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getContentHash() {
        return contentHash;
    }

    public long getSignTime() {
        return signTime;
    }

    public BookCheckResult check(BookMeta bookMeta) {
        if (bookMeta == null) {
            return BookCheckResult.UNSIGNED;
        }

        if (!bookMeta.getAuthor().equals(author)) {
            return BookCheckResult.AUTHOR_CHANGED;
        }

        if (!BookSignatureUtil.getContentHash(bookMeta).equals(contentHash)) {
            return BookCheckResult.CONTENT_CHANGED;
        }

        return BookCheckResult.OK;
    }

    public String encodeJson() {
        return GSON.toJson(this);
    }

    public static BookSignature decodeJson(String json) {
        return GSON.fromJson(json, BookSignature.class);
    }

}
