package com.ruinscraft.bookverify;

import com.google.gson.Gson;
import org.bukkit.inventory.meta.BookMeta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BookSignature {

    private static final Gson GSON = new Gson();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM d, yyyy");

    private String title;
    private String author;
    private String contentHash;
    private long signTime;

    public BookSignature(BookMeta bookMeta) {
        title = bookMeta.getTitle();
        author = bookMeta.getAuthor();
        contentHash = BookSignatureUtil.getContentHash(bookMeta);
        signTime = System.currentTimeMillis();
    }

    public BookSignature(String title, String author, String contentHash, long signTime) {
        this.title = title;
        this.author = author;
        this.contentHash = contentHash;
        this.signTime = signTime;
    }

    public static BookSignature decodeJson(String json) {
        return GSON.fromJson(json, BookSignature.class);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContentHash() {
        return contentHash;
    }

    public long getSignTime() {
        return signTime;
    }

    public String getSignDateHuman() {
        Date date = new Date(signTime);
        return DATE_FORMAT.format(date);
    }

    public Set<BookSignatureElement> getChangedElements(BookMeta bookMeta) {
        Set<BookSignatureElement> changedElements = new HashSet<>();

        if (!title.equals(bookMeta.getTitle())) {
            changedElements.add(BookSignatureElement.TITLE);
        }

        if (!author.equals(bookMeta.getAuthor())) {
            changedElements.add(BookSignatureElement.AUTHOR);
        }

        if (!contentHash.equals(BookSignatureUtil.getContentHash(bookMeta))) {
            changedElements.add(BookSignatureElement.CONTENT_HASH);
        }

        return changedElements;
    }

    public String encodeJson() {
        return GSON.toJson(this);
    }

    @Override
    public String toString() {
        return String.format("Title: %s, Author: %s, Date: %s", title, author, getSignDateHuman());
    }

}
