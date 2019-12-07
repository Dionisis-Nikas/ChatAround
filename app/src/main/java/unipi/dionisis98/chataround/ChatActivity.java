package unipi.dionisis98.chataround;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private String ChatUser;
    private DatabaseReference RootRef;
    private FirebaseAuth firebaseAuth;
    private String curUserId;
    private String name;
    private EditText messageText;
    private ImageButton send;
    private ArrayList<String> messages;
    private ListView listView;//Our listview
    private ArrayAdapter adapter;//the adapter to put the array list data to our listview
    private FirebaseUser firebaseUser;
    private String chatroom;
    private String user_msg_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            chatroom = getIntent().getStringExtra("topic");
        } catch (Exception e) {
            e.printStackTrace();
            Intent sendToLogin = new Intent(getApplicationContext(),StartActivity.class);
            startActivity(sendToLogin);
        }

        setContentView(R.layout.activity_chat);
        String uid = firebaseUser.getUid().toString();
        DatabaseReference name = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("name");
        name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.getValue().toString();
                ChatUser = user_name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setTitle(chatroom+" Chatroom");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        RootRef = FirebaseDatabase.getInstance().getReference().child("Chatrooms").child(chatroom);
        send = findViewById(R.id.sendBtn);
        listView = findViewById(R.id.listview);
        messageText = findViewById(R.id.messageText);
        messages = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,messages);
        listView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> messageMap = new HashMap<String, Object>();
                user_msg_key = RootRef.push().getKey().toString();
                RootRef.updateChildren(messageMap);

                DatabaseReference message = RootRef.child(user_msg_key);
                Map<String,Object> messageMap2 = new HashMap<String, Object>();
                messageMap2.put("user",ChatUser);
                messageMap2.put("message",messageText.getText().toString());
                message.updateChildren(messageMap2);
            }
        });

        RootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateList(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateList(DataSnapshot dataSnapshot) {
        String message = "",user = "",conv;
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            message = (String)((DataSnapshot)i.next()).getValue().toString();
            user = (String)((DataSnapshot)i.next()).getValue().toString();
        }
        conv = user + ": " + message;
        adapter.insert(conv,adapter.getCount());
        adapter.notifyDataSetChanged();
    }
}
