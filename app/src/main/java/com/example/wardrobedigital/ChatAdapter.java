package com.example.wardrobedigital;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messages;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvSponsor;
        LinearLayout messageContainer;

        MessageViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvSponsor = itemView.findViewById(R.id.tv_sponsor);
            messageContainer = itemView.findViewById(R.id.message_container);
        }

        void bind(Message message) {
            // ✅ Clean markdown formatting dari AI
            String cleanedMessage = cleanMarkdown(message.getContent());
            tvMessage.setText(cleanedMessage);

            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();

            if (message.isUser()) {
                // User message - align right
                params.gravity = Gravity.END;
                messageContainer.setBackgroundResource(R.drawable.bg_message_user);
                tvSponsor.setVisibility(View.GONE);
            } else {
                // AI message - align left
                params.gravity = Gravity.START;
                messageContainer.setBackgroundResource(R.drawable.bg_message_ai);
                tvSponsor.setVisibility(View.VISIBLE);
            }

            messageContainer.setLayoutParams(params);
        }

        /**
         * Clean markdown formatting dari text
         */
        private String cleanMarkdown(String text) {
            if (text == null) return "";

            // Remove bold (**text** atau __text__)
            text = text.replaceAll("\\*\\*(.*?)\\*\\*", "$1");
            text = text.replaceAll("__(.*?)__", "$1");

            // Remove italic (*text* atau _text_) - hati-hati dengan *
            text = text.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "$1");
            text = text.replaceAll("(?<!_)_(?!_)(.*?)_(?!_)", "$1");

            // Remove strikethrough (~~text~~)
            text = text.replaceAll("~~(.*?)~~", "$1");

            // Remove code blocks (```code```)
            text = text.replaceAll("```[\\s\\S]*?```", "[code]");
            text = text.replaceAll("`(.*?)`", "$1");

            // Remove headers (# Header)
            text = text.replaceAll("^#{1,6}\\s+", "");
            text = text.replaceAll("\n#{1,6}\\s+", "\n");

            // Remove horizontal rules (---, ***)
            text = text.replaceAll("^[-*=]{3,}$", "");
            text = text.replaceAll("\n[-*=]{3,}\n", "\n");

            // Remove bullet points and list markers
            text = text.replaceAll("^[*+-]\\s+", "• ");
            text = text.replaceAll("\n[*+-]\\s+", "\n• ");

            // Clean up excessive newlines
            text = text.replaceAll("\n{3,}", "\n\n");

            return text.trim();
        }
    }
}