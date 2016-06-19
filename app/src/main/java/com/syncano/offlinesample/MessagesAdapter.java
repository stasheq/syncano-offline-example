package com.syncano.offlinesample;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final long ID_UNKNOWN = -1;
    private static final long ID_LOADING = -2;
    private static final int TYPE_LOADING = 0;
    private static final int TYPE_MESSAGE = 1;

    private ArrayList<Message> messages = new ArrayList<>();
    private boolean loading = false;
    private View.OnClickListener itemClickListener;

    public MessagesAdapter(View.OnClickListener itemClickListener) {
        setHasStableIds(true);
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_LOADING:
                return new LoadingViewHolder(LoadingViewHolder.inflateView(parent));
            case TYPE_MESSAGE:
                return new MessageItemViewHolder(MessageItemViewHolder.inflateView(parent), itemClickListener);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case TYPE_LOADING:
                return ID_LOADING;
            case TYPE_MESSAGE:
                return messages.get(position).getLocalId();
        }
        return ID_UNKNOWN;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == messages.size()) {
            return TYPE_LOADING;
        }
        return TYPE_MESSAGE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_MESSAGE:
                ((MessageItemViewHolder) holder).prepareView(messages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        int count = messages.size();
        if (isLoading()) {
            count++;
        }
        return count;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyDataSetChanged();
    }

    public void setMessages(List<Message> messages) {
        if (messages == null) {
            this.messages = new ArrayList<>();
        } else {
            this.messages = new ArrayList<>(messages);
        }
        Collections.sort(this.messages);
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        // checks first if message isn't already on the list
        int currentIndex = -1;
        for (int i = 0; i < messages.size(); i++) {
            Integer id = messages.get(i).getId();
            if (id != null && id.equals(message.getId())) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) {
            messages.add(message);
        } else {
            messages.set(currentIndex, message);
        }
        notifyDataSetChanged();
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }

        public static View inflateView(ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item, parent, false);
        }
    }
}
