package unipi.dionisis98.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout dispName;
    private TextInputLayout passwordTextEdit;
    private TextInputLayout emailTextEdit;
    private Button regCreateBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView progressText;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dispName = findViewById(R.id.dispNameTextView);
        passwordTextEdit = findViewById(R.id.passwordTextView);
        emailTextEdit = findViewById(R.id.emailTextView);
        regCreateBtn = findViewById(R.id.regCreateBtn);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        mAuth = FirebaseAuth.getInstance();
        regCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = dispName.getEditText().getText().toString();
                String email = emailTextEdit.getEditText().getText().toString();
                String password = passwordTextEdit.getEditText().getText().toString();

                if ( !(TextUtils.isEmpty(username)) && !(TextUtils.isEmpty(email)) && !(TextUtils.isEmpty(password)) && password.length()>=6){
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressText.setVisibility(View.VISIBLE);
                    registerUser(username,email,password);
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                    showMessage("Registration Failed","The creation of your account failed. Please check your connection " +
                            "or the credentials you gave us. Remember that the password should be bigger than 6 characters");
                }

            }
        });

    }

    private void registerUser(final String username, final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid().toString();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",username);
                    userMap.put("status","Hi there lets chat");
                    userMap.put("image","default");
                    userMap.put("thumbImage","default");

                    databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                // Sign in success, update UI with the signed-in user's information
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                //updateUI(user);

                            }
                            else {
                                showMessage("Error","Database connection failed");
                            }
                        }
                    });


                } else {
                    // If sign in fails, display a message to the user.
                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Failed Registration", e);
                }
            }
        });
    }

    //the method to show our alert dialog message
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title)
                .setMessage(message);
        builder.show();
    }
}
