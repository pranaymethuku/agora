package com.agora.android.agora;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class AllChatsFragment extends Fragment {

    private final String CHATS = "chats";
    private final String RECENT = "recent";
    private final String USERS = "users";

    private RecyclerView mChatsRecyclerView;


    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore mFirestore;
    private FirestoreRecyclerAdapter<ChatItem, ChatItemViewHolder> mFirestoreRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    public class ChatItemViewHolder extends RecyclerView.ViewHolder {

        TextView messengerTextView;
        TextView lastMessageTextView;

        public ChatItemViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.chat_item, parent, false));
            messengerTextView = (TextView) itemView.findViewById(R.id.text_view_other_user);
            lastMessageTextView = (TextView) itemView.findViewById(R.id.text_view_last_message);
        }
    }

    public AllChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirestore = FirebaseFirestore.getInstance();
        final Query recentChats = mFirestore.collection(CHATS)
                .document("1JtcGMQwt1IqFCaeSBPG")
                .collection(RECENT);

        SnapshotParser<ChatItem> parser = new SnapshotParser<ChatItem>() {
            @NonNull
            @Override
            public ChatItem parseSnapshot(DocumentSnapshot snapshot) {
                ChatItem chatItem = new ChatItem();
                chatItem.setId(snapshot.getId());
                chatItem.setName(snapshot.getString("display_name"));
                chatItem.setLastMessage(snapshot.getString("last_message"));
                return chatItem;
            }
        };


        // Configure recycler adapter options:
        // chats is the Query object defined above.
        // ChatItem.class instructs the adapter to convert each DocumentSnapshot to a Chat object
        FirestoreRecyclerOptions<ChatItem> options = new FirestoreRecyclerOptions.Builder<ChatItem>()
                .setQuery(recentChats, parser)
                .build();

        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ChatItem, ChatItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatItemViewHolder viewHolder, int position,
                                            @NonNull ChatItem chatItem) {
                if (chatItem.getLastMessage() != null) {
                    viewHolder.lastMessageTextView.setText(chatItem.getLastMessage());
                }
                viewHolder.messengerTextView.setText(chatItem.getName());
            }

            @NonNull
            @Override
            public ChatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                return new ChatItemViewHolder(inflater, parent);
            }

        };

        mFirestoreRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirestoreRecyclerAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mChatsRecyclerView.scrollToPosition(positionStart);
                }
            }
        });


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_chats, container, false);

        mChatsRecyclerView = (RecyclerView) view.findViewById(R.id.chats_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        mChatsRecyclerView.setAdapter(mFirestoreRecyclerAdapter);
        mChatsRecyclerView.setLayoutManager(mLinearLayoutManager);

        return view;
    }
}
