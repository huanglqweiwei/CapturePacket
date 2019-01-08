package net.lightbody.bmp.core.har;

/**
 * Created by hlq on 2018/12/23 0023.
 */

public interface HarCallback {
    int ITEM_REQUEST = 0;
    int ITEM_RESPONSE = 1;
    void onAddEntry(HarEntry harEntry, int position);
    void onEntryChanged(HarEntry harEntry,int changeItem);

    void onClearEntries();
}
