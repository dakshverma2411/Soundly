package daksh.soundly.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import daksh.soundly.MainActivity;
import daksh.soundly.R;

public class LoginActivity extends AppCompatActivity {

    EditText Email;
    EditText Password;
    Button login;
    TextView register;
    String email;
    String password;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email=(EditText) findViewById(R.id.login_email);
        Password=(EditText) findViewById(R.id.login_password);
        login=(Button) findViewById(R.id.login_login_button);
        register=(TextView) findViewById(R.id.login_goto_register);
        progressBar=(ProgressBar) findViewById(R.id.progressBarLogin);
        progressBar.setVisibility(View.GONE);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginClicked();
                login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        mAuth=FirebaseAuth.getInstance();
    }

    public void onLoginClicked()
    {
        email=Email.getText().toString().trim();
        password=Password.getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter a email",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length()<6)
        {
            Toast.makeText(this,"Password must be atleast 6 characters long",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this,"Logged In",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}