package com.syncano.offlinesample;

import android.support.annotation.NonNull;

import com.syncano.library.annotation.SyncanoClass;
import com.syncano.library.annotation.SyncanoField;
import com.syncano.library.data.SyncanoObject;

@SyncanoClass(name = "messages", saveDownloadedDataToStorage = true)
public class Message extends SyncanoObject implements Comparable<Message> {
    @SyncanoField(name = "text")
    public String text;
    @SyncanoField(name = "error_sending", onlyLocal = true)
    public boolean errorSending = false;

    @Override
    // for sorting purposes
    public int compareTo(@NonNull Message another) {
        if (getId() == null && another.getId() != null) return 1;
        if (getId() != null && another.getId() == null) return -1;
        if (getId() != null && another.getId() != null) return getId() - another.getId();
        return (int) (getLocalId() - another.getLocalId());
    }
}
