package com.ruinscraft.bookverify;

public enum BookCheckResult {

    OK(),
    UNSIGNED(),
    AUTHOR_CHANGED(),
    CONTENT_CHANGED(),
    AUTHOR_CONTENT_CHANGED();

}
