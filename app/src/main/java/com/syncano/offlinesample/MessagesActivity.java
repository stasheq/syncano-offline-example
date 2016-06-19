

package com.syncano.offlinesample;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MessagesActivity extends NoConnectionActivity implements View.OnClickListener {

    private MessagesIO messagesIO;
    private RecyclerView recycler;
    private MessagesAdapter adapter;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_list_activity);

        messagesIO = new MessagesIO(this);
        checkConnectionState();
        initRecyclerView();
        input = (EditText) findViewById(R.id.mlist_your_message_text);
        findViewById(R.id.mlist_send_button).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messagesIO.start();
        messagesIO.downloadMessages();
    }

    @Override
    protected void onPause() {
        messagesIO.stop();
        super.onPause();
    }

    private void initRecyclerView() {
        recycler = (RecyclerView) findViewById(R.id.mlist_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MessagesAdapter(new MessageClickListener());
        recycler.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mlist_send_button:
                Message m = new Message();
                m.text = input.getText().toString();
                addMessageToList(m);
                messagesIO.sendMessage(m);
                input.setText("");
                break;
        }
    }

    @Override
    public int getNoConnectionViewId() {
        return R.id.no_internet_label;
    }

    private void scrollListToLastPosition() {
        recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    /*
     * Below methods are called from MessagesIO
     */

    public void setMessages(List<Message> messages) {
        adapter.setMessages(messages);
        scrollListToLastPosition();
    }

    public void addMessageToList(Message m) {
        adapter.addMessage(m);
        scrollListToLastPosition();
    }

    public void setLoading(boolean loading) {
        adapter.setLoading(loading);
        scrollListToLastPosition();
    }

    public void showDownloadingError() {
        checkConnectionState();
        Toast.makeText(MessagesActivity.this, R.string.error_downloading, Toast.LENGTH_SHORT).show();
    }

    /*
     * End of methods called from MessagesIO
     */

    private class MessageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Message message = (Message) v.getTag();
            message.errorSending = false;
            messagesIO.sendMessage(message);
        }
    }
}
