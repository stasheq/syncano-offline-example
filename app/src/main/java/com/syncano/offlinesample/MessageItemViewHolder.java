package com.syncano.offlinesample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageItemViewHolder extends RecyclerView.ViewHolder {

    private View layout;
    private TextView textView;
    private View loadingView;
    private View errorView;
    private View.OnClickListener itemClickListener;

    public MessageItemViewHolder(View view, View.OnClickListener itemClickListener) {
        super(view);
        layout = view.findViewById(R.id.mitem_layout);
        textView = (TextView) view.findViewById(R.id.mitem_text);
        loadingView = view.findViewById(R.id.mitem_loading);
        errorView = view.findViewById(R.id.mitem_error);
        this.itemClickListener = itemClickListener;
    }

    public static View inflateView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
    }

    public void prepareView(Message m) {
        textView.setText(m.text);

        switch (checkMode(m)) {
            case SENT:
                layout.setOnClickListener(null);
                layout.setBackgroundResource(R.drawable.message_item_background);
                errorView.setVisibility(View.INVISIBLE);
                loadingView.setVisibility(View.INVISIBLE);
                break;
            case FAILED:
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        errorView.setVisibility(View.INVISIBLE);
                        loadingView.setVisibility(View.VISIBLE);
                        itemClickListener.onClick(v);
                    }
                });
                layout.setTag(m);
                layout.setBackgroundResource(R.drawable.message_failed_item_background);
                errorView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.INVISIBLE);
                break;
            case SENDING:
                layout.setOnClickListener(null);
                layout.setBackgroundResource(R.drawable.message_item_background);
                errorView.setVisibility(View.INVISIBLE);
                loadingView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private Mode checkMode(Message m) {
        if (m.errorSending) {
            return Mode.FAILED;
        } else {
            if (m.getId() == null) {
                return Mode.SENDING;
            } else {
                return Mode.SENT;
            }
        }
    }

    private enum Mode {
        SENT, FAILED, SENDING
    }
}
