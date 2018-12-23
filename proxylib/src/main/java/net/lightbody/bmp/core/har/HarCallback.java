package net.lightbody.bmp.core.har;

/**
 * Created by hlq on 2018/12/23 0023.
 */

public interface HarCallback {
    void onAddEntry(HarEntry harEntry, int position);
    void onEntryChanged(HarEntry harEntry);
}
