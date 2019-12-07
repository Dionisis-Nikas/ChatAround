package unipi.dionisis98.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbr;
    private ListView discTopicsListView;
    private ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = firebaseUser.getUid().toString();
            DatabaseReference name = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("name");
            name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user_name = dataSnapshot.getValue().toString();
                    getSupportActionBar().setTitle("ChatAround - User: "+user_name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mAuth = FirebaseAuth.getInstance();

            dbr = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
            discTopicsListView = findViewById(R.id.discTopicsListView);

            arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
            discTopicsListView.setAdapter(arrayAdapter);
            dbr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Set<String> set = new HashSet<String>();
                    Iterator i = dataSnapshot.getChildren().iterator();

                    while (i.hasNext()){
                        set.add(((DataSnapshot)i.next()).getKey());
                    }

                    arrayAdapter.clear();
                    arrayAdapter.addAll(set);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            discTopicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                    i.putExtra("topic",((TextView)view).getText().toString());
                    i.putExtra("user",mAuth.getCurrentUser().getUid().toString());
                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            goToStart();
        }
    }



    private void goToStart() {
        Intent intent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.mainLogOutBtn){
            FirebaseAuth.getInstance().signOut();
            goToStart();
        }
        return true;
    }
}
