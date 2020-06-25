package com.example.rideeinhands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.databinding.ActivityChatBinding;
import com.example.rideeinhands.models.Message;
import com.example.rideeinhands.models.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    public static ArrayList<Message> messagesList;
    ActivityChatBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String no_of_passengers;
    RecyclerView recyclerView;
    private FirebaseFunctions mFunctions;
    FirestoreRecyclerAdapter<Message, ChatActivity.ChatViewHolder> firestoreRecyclerAdapter;


    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFunctions = FirebaseFunctions.getInstance();

        FirebaseFirestore.getInstance().collection("Users").document(getIntent().getStringExtra("With"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                getSupportActionBar().setTitle(documentSnapshot.getString("Name"));

            }
        });

        recyclerView = binding.recyclerView;
        firebaseFirestore = FirebaseFirestore.getInstance();
        messagesList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        String with = getIntent().getStringExtra("With");
        Query query = firebaseFirestore.collection("Chats").document(FirebaseAuth.getInstance().getUid())
                .collection(with).orderBy("TimeStamp");
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Message, ChatActivity.ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatActivity.ChatViewHolder chatViewHolder, int i, @NonNull Message message) {
                chatViewHolder.setMessage(message.getText(), message.getType());
                recyclerView.scrollToPosition(firestoreRecyclerAdapter.getItemCount()-1);

            }

            @NonNull
            @Override
            public ChatActivity.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
                recyclerView.scrollToPosition(firestoreRecyclerAdapter.getItemCount()-1);

                return new ChatActivity.ChatViewHolder(view);
            }
        };
        firestoreRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firestoreRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setAdapter(firestoreRecyclerAdapter);
        recyclerView.scrollToPosition(firestoreRecyclerAdapter.getItemCount()-1);


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.msg.getText().toString().isEmpty()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("TimeStamp", FieldValue.serverTimestamp());
                    map.put("Type", "Sent");
                    map.put("Text", binding.msg.getText().toString());
                    HashMap<String, Object> map1 = new HashMap<>();
                    map1.put("TimeStamp", FieldValue.serverTimestamp());
                    map1.put("Type", "Received");
                    map1.put("Text", binding.msg.getText().toString());
                    Toast.makeText(ChatActivity.this, getIntent().getStringExtra("With"), Toast.LENGTH_SHORT).show();
                    FirebaseFirestore.getInstance().collection("Chats").document(FirebaseAuth.getInstance().getUid())
                            .collection(getIntent().getStringExtra("With")).add(map);
                    FirebaseFirestore.getInstance().collection("Chats").document(getIntent().getStringExtra("With"))
                            .collection(FirebaseAuth.getInstance().getUid()).add(map1);
                    FirebaseFirestore.getInstance().collection("Users").document(getIntent().getStringExtra("With"))
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            sendNotification(documentSnapshot.getString("Name"),documentSnapshot.getString("DeviceToken"),
                                    binding.msg.getText().toString(), getIntent().getStringExtra("With")).addOnSuccessListener(new OnSuccessListener<HashMap<String, String>>() {
                                @Override
                                public void onSuccess(HashMap<String, String> stringStringHashMap) {
                                    recyclerView.scrollToPosition(firestoreRecyclerAdapter.getItemCount()-1);

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

    }

    private class ChatViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setMessage(String text, String type) {
            TextView textView1 = view.findViewById(R.id.message1);
            TextView textView2 = view.findViewById(R.id.message2);
            if (type.equals("Sent")) {
                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(text);
            } else if (type.equals("Received")) {
                textView2.setVisibility(View.GONE);
                textView1.setVisibility(View.VISIBLE);
                textView1.setText(text);
            }
        }


    }
    private Task<HashMap<String,String>> sendNotification(String name, String device_token, String message, String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("token_id", device_token);
        data.put("msg", message);
        data.put("uid", getIntent().getStringExtra("With"));
        return mFunctions
                .getHttpsCallable("sendNotification")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String,String>>() {
                    @Override
                    public HashMap<String,String> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String,String> result = (HashMap<String,String>)task.getResult().getData();
                        return result;
                    }
                });
    }
}
