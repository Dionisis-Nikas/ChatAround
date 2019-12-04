package unipi.dionisis98.chataround;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout passwordTextEdit;
    private TextInputLayout emailTextEdit;
    private Button logInBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView progressText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passwordTextEdit = findViewById(R.id.passwordTextView);
        emailTextEdit = findViewById(R.id.emailTextView);
        logInBtn = findViewById(R.id.regCreateBtn);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        mAuth = FirebaseAuth.getInstance();
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextEdit.getEditText().getText().toString();
                String password = passwordTextEdit.getEditText().getText().toString();

                if (!(TextUtils.isEmpty(email)) && !(TextUtils.isEmpty(password)) && password.length()>=6){
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressText.setVisibility(View.VISIBLE);
                    logUser(email,password);
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                    showMessage("Login Failed","Login failed. Please check your connection " +
                            "or the credentials you gave and try again. Remember that the password should be bigger than 6 characters");
                }

            }
        });

    }

    private void logUser(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Toast.makeText(LoginActivity.this, "Login failed.",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                    showMessage("Login Failed","Wrong password. Please try again");
                    Log.e("LoginActivity", "Failed Login", e);
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
