package com.example.wardrobedigital;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardrobedigital.api.OllamaApiClient;
import com.google.android.material.chip.Chip;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar progressBar;
    private ChatAdapter adapter;
    private List<Message> messages;
    private WardrobeHelper wardrobeHelper;
    private AIPromptBuilder promptBuilder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wardrobeHelper = new WardrobeHelper(requireContext());
        promptBuilder = new AIPromptBuilder(requireContext());

        rvChat = view.findViewById(R.id.rv_chat);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);
        progressBar = view.findViewById(R.id.progress_bar);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);

        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        // ✅ Setup shortcut chips
        setupShortcuts(view);

        loadChatHistory();
        btnSend.setOnClickListener(v -> sendMessage());
    }

    /**
     * Setup shortcut buttons
     */
    private void setupShortcuts(View view) {
        Chip chipMyClothes = view.findViewById(R.id.chip_my_clothes);
        Chip chipMyOutfits = view.findViewById(R.id.chip_my_outfits);
        Chip chipCreateOutfit = view.findViewById(R.id.chip_create_outfit);
        Chip chipFashionTips = view.findViewById(R.id.chip_fashion_tips);

        chipMyClothes.setOnClickListener(v -> {
            sendQuickMessage("Apa saja baju yang kumiliki?");
        });

        chipMyOutfits.setOnClickListener(v -> {
            sendQuickMessage("Apa saja outfit yang kumiliki?");
        });

        chipCreateOutfit.setOnClickListener(v -> {
            sendQuickMessage("Buatkan outfit untuk aku");
        });

        chipFashionTips.setOnClickListener(v -> {
            sendQuickMessage("Kasih tips fashion dong");
        });
    }

    /**
     * Send pre-defined quick message
     */
    private void sendQuickMessage(String message) {
        etMessage.setText(message);
        sendMessage();
    }

    private void loadChatHistory() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<MessageEntity> entities = db.messageDao().getAllMessages();

        messages.clear();
        for (MessageEntity entity : entities) {
            messages.add(new Message(
                    entity.getId(),
                    entity.getContent(),
                    entity.getType(),
                    entity.getTimestamp()
            ));
        }
        adapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void sendMessage() {
        String userMessage = etMessage.getText().toString().trim();
        if (userMessage.isEmpty()) {
            Toast.makeText(getContext(), "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message
        Message msg = new Message(userMessage, Message.TYPE_USER, System.currentTimeMillis());
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        // Save to database
        AppDatabase db = AppDatabase.getInstance(requireContext());
        MessageEntity entity = new MessageEntity(userMessage, Message.TYPE_USER, System.currentTimeMillis());
        db.messageDao().insertMessage(entity);

        // Clear input
        etMessage.setText("");

        // Send to AI
        sendToOllama(userMessage);
    }

    private void sendToOllama(String userMessage) {
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        // ✅ Rebuild promptBuilder untuk data terbaru
        promptBuilder = new AIPromptBuilder(requireContext());

        // Build request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "qwen2.5:14b");

        // Build messages array with history
        JsonArray messagesArray = new JsonArray();

        // ✅ SYSTEM PROMPT - Selalu fresh dengan data terbaru
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", promptBuilder.buildSystemPrompt());
        messagesArray.add(systemMsg);

        // Add chat history (user & assistant only)
        for (Message msg : messages) {
            JsonObject msgObj = new JsonObject();
            msgObj.addProperty("role", msg.isUser() ? "user" : "assistant");
            msgObj.addProperty("content", msg.getContent());
            messagesArray.add(msgObj);
        }

        requestBody.add("messages", messagesArray);
        requestBody.addProperty("stream", false);

        // API call
        OllamaApiClient.getService().sendChat(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject responseBody = response.body();
                        String aiReply = responseBody.getAsJsonObject("message")
                                .get("content").getAsString();

                        // ✅ Check for special commands
                        processAIResponse(aiReply);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "AI tidak merespon: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                Toast.makeText(getContext(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Process AI response dan handle special commands
     */
    private void processAIResponse(String aiReply) {
        // Check for SHOW_WARDROBE command
        if (aiReply.contains("SHOW_WARDROBE")) {
            String wardrobeList = wardrobeHelper.getClothingListForPrompt();
            aiReply = aiReply.replace("SHOW_WARDROBE", wardrobeList);
        }

        // Check for CREATE_OUTFIT command
        Pattern pattern = Pattern.compile("CREATE_OUTFIT:\\s*([^|]+)\\|\\s*(\\d+|null)\\|\\s*(\\d+|null)\\|\\s*(\\d+)\\|\\s*(\\d+)\\|\\s*(\\d+|null)");
        Matcher matcher = pattern.matcher(aiReply);

        if (matcher.find()) {
            String outfitName = matcher.group(1).trim();
            String outerIdStr = matcher.group(2).trim();
            String innerIdStr = matcher.group(3).trim();
            int bawahanId = Integer.parseInt(matcher.group(4).trim());
            int sepatuId = Integer.parseInt(matcher.group(5).trim());
            String aksesorisIdStr = matcher.group(6).trim();

            Integer outerId = outerIdStr.equals("null") ? null : Integer.parseInt(outerIdStr);
            Integer innerId = innerIdStr.equals("null") ? null : Integer.parseInt(innerIdStr);
            Integer aksesorisId = aksesorisIdStr.equals("null") ? null : Integer.parseInt(aksesorisIdStr);

            // ✅ Check duplikasi
            if (wardrobeHelper.isOutfitDuplicate(outerId, innerId, bawahanId, sepatuId)) {
                aiReply = matcher.replaceAll("⚠️ Outfit dengan kombinasi ini sudah ada! Mau coba kombinasi lain?");
            } else {
                // Create outfit
                boolean success = wardrobeHelper.createOutfit(outfitName, outerId, innerId, bawahanId, sepatuId, aksesorisId);

                if (success) {
                    // Remove command dari response
                    aiReply = matcher.replaceAll("✅ Outfit '" + outfitName + "' berhasil dibuat!\n\n" +
                            "📍 Cek di tab Outfit untuk melihat hasil kombinasinya!");

                    Toast.makeText(getContext(), "✅ Outfit '" + outfitName + "' dibuat!", Toast.LENGTH_LONG).show();

                    // ✅ Auto navigate ke Outfit tab setelah 2 detik
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        if (getActivity() != null) {
                            com.google.android.material.bottomnavigation.BottomNavigationView navView =
                                    getActivity().findViewById(R.id.bottom_nav_view);
                            if (navView != null) {
                                navView.setSelectedItemId(R.id.nav_outfit);
                            }
                        }
                    }, 2000);

                } else {
                    aiReply = matcher.replaceAll("❌ Gagal membuat outfit. Pastikan ID item yang dipilih valid.");
                }
            }
        }

        // Add AI message
        Message aiMsg = new Message(aiReply, Message.TYPE_AI, System.currentTimeMillis());
        messages.add(aiMsg);
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        // Save to database
        AppDatabase db = AppDatabase.getInstance(requireContext());
        MessageEntity aiEntity = new MessageEntity(aiReply, Message.TYPE_AI, System.currentTimeMillis());
        db.messageDao().insertMessage(aiEntity);
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            rvChat.smoothScrollToPosition(messages.size() - 1);
        }
    }
}