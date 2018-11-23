package com.agora.android.agora;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ChatFragment extends Fragment {

    private final String MESSAGES = "messages";
    private final String HISTORY = "history";

    private EditText mMessageInput;
    private RecyclerView mChatsRecyclerView;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestore;
    private FirestoreRecyclerAdapter<ChatMessage, ChatMessageViewHolder> mFirestoreRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public ChatFragment() {
        // Required empty public constructor
    }

    public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;

        public ChatMessageViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.chat_message, parent, false));
            messageTextView = (TextView) itemView.findViewById(R.id.message_body);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirestore = FirebaseFirestore.getInstance();
        final Query recentChats = mFirestore.collection(MESSAGES)
                .document("1JtcGMQwt1IqFCaeSBPG-dvtKHblwGnXMViUJzNbS")
                .collection(HISTORY);

        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @NonNull
            @Override
            public ChatMessage parseSnapshot(DocumentSnapshot snapshot) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(snapshot.getId());
                chatMessage.setSender(snapshot.getString("sender"));
                chatMessage.setText(snapshot.getString("text"));
                return chatMessage;
            }
        };


        // Configure recycler adapter options:
        // chats is the Query object defined above.
        // ChatItem.class instructs the adapter to convert each DocumentSnapshot to a Chat object
        final FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(recentChats, parser)
                .build();

        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatMessageViewHolder viewHolder, int position,
                                            @NonNull ChatMessage chatMessage) {
                if (chatMessage.getSender().equals("dvtKHblwGnXMViUJzNbS")) {
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.my_message);
                } else {
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.their_message);
                }
                if (chatMessage.getId() != null) {
                    viewHolder.messageTextView.setText(chatMessage.getText());
                }
            }

            @NonNull
            @Override
            public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new ChatMessageViewHolder(inflater, parent);
            }

        };

        mFirestoreRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mFirestoreRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mChatsRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

    }

    private int dpToPx(int dp) {
        float density = getContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onPause() {
        mFirestoreRecyclerAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirestoreRecyclerAdapter.startListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mChatsRecyclerView = (RecyclerView) view.findViewById(R.id.messages_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        mChatsRecyclerView.setAdapter(mFirestoreRecyclerAdapter);
        mChatsRecyclerView.setLayoutManager(mLinearLayoutManager);

        return view;
    }
}
