package com.syncano.offlinesample;

import android.util.Log;

import com.syncano.library.ChannelConnection;
import com.syncano.library.ChannelConnectionListener;
import com.syncano.library.Syncano;
import com.syncano.library.api.Response;
import com.syncano.library.api.ResponseGetList;
import com.syncano.library.callbacks.SyncanoCallback;
import com.syncano.library.callbacks.SyncanoListCallback;
import com.syncano.library.choice.NotificationAction;
import com.syncano.library.choice.SortOrder;
import com.syncano.library.data.Entity;
import com.syncano.library.data.Notification;
import com.syncano.library.offline.OfflineMode;

import java.util.List;

/**
 * Contents of this class could be part of MessagesActivity. This separate class was created
 * just to keep this code clean and easier to read.
 */
public class MessagesIO {
    private final static String CHANNEL_NAME = "messages_changes";
    private MessagesActivity activity;
    private ChannelConnection channelConnection;
    private boolean paused = true;

    public MessagesIO(MessagesActivity messagesActivity) {
        this.activity = messagesActivity;
    }

    /**
     * Called when activity is shown to enable asynchronous callbaks and start realtime channel connection.
     */
    public void start() {
        if (!paused) {
            return;
        }
        paused = false;
        startChannelConnection();
    }

    /**
     * Called when Activity is getting paused, to not return any asynchronous callbacks to Activity
     * and to stop realtime channel connection.
     */
    public void stop() {
        if (paused) {
            return;
        }
        paused = true;
        channelConnection.stop();
        channelConnection = null;
    }

    private void startChannelConnection() {
        channelConnection = new ChannelConnection(Syncano.getInstance(), channelListener);
        channelConnection.setAutoReconnect(true);
        channelConnection.start(CHANNEL_NAME);
    }

    public void sendMessage(final Message m) {
        m.setChannel(CHANNEL_NAME);
        // save message in local db, and send to Syncano in background
        m.mode(OfflineMode.LOCAL_ONLINE_IN_BACKGROUND).backgroundCallback(new SyncanoCallback<Message>() {
            @Override
            public void success(Response<Message> response, Message message) {
                refreshMessagesFromLocalDb();
            }

            @Override
            public void failure(Response<Message> response) {
                m.errorSending = true;
                m.mode(OfflineMode.LOCAL).save();
                refreshMessagesFromLocalDb();
            }
        }).save();
        // message is saved to db immediately in above line, callback is returned for online call
    }

    public void downloadMessages() {
        activity.setLoading(true);
        Syncano.please(Message.class).mode(OfflineMode.ONLINE)
                .orderBy(Entity.FIELD_UPDATED_AT, SortOrder.DESCENDING)
                .get(new SyncanoListCallback<Message>() {
                    @Override
                    public void success(ResponseGetList<Message> responseGetList, List<Message> list) {
                        refreshMessagesFromLocalDb();
                    }

                    @Override
                    public void failure(ResponseGetList<Message> responseGetList) {
                        if (paused) return;
                        refreshMessagesFromLocalDb();
                        activity.showDownloadingError();
                    }
                });
    }

    private void refreshMessagesFromLocalDb() {
        Syncano.please(Message.class).mode(OfflineMode.LOCAL).get(new SyncanoListCallback<Message>() {
            @Override
            public void success(ResponseGetList<Message> responseGetList, List<Message> list) {
                if (paused) return;
                activity.setMessages(list);
                activity.setLoading(false);
            }

            @Override
            public void failure(ResponseGetList<Message> responseGetList) {
                // not expected :)
            }
        });
    }

    private ChannelConnectionListener channelListener = new ChannelConnectionListener() {
        private boolean wasError = false;

        @Override
        public void onNotification(Notification n) {
            Log.d(ChannelConnectionListener.class.getSimpleName(), "Received channel notification");
            if (wasError) {
                activity.checkConnectionState();
                wasError = false;
            }
            if (n.getAction() == NotificationAction.CREATE) {
                Message m = n.getPayloadAs(Message.class);
                if (m != null) {
                    m.mode(OfflineMode.LOCAL).save();
                    activity.addMessageToList(m);
                }
            } else {
                downloadMessages();
            }
        }

        @Override
        public void onError(Response<Notification> response) {
            Log.d(ChannelConnectionListener.class.getSimpleName(), "Received error");
            if (!wasError) {
                activity.checkConnectionState();
                wasError = true;
            }
        }
    };

}
