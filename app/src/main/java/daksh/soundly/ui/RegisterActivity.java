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

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    String email;
    String password;
    String name;
    EditText Email;
    EditText Password;
    EditText Name;
    Button register;
    TextView login;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Email=(EditText) findViewById(R.id.register_email);
        Password=(EditText) findViewById(R.id.register_password);
        Name=(EditText) findViewById(R.id.register_name);
        register=(Button) findViewById(R.id.register_button);
        login=(TextView) findViewById(R.id.register_goto_login);
        mAuth=FirebaseAuth.getInstance();
        progressBar=(ProgressBar) findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.GONE);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegistrationClicked();
                register.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null)
        {
            Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void onRegistrationClicked()
    {
        email=Email.getText().toString().trim();
        password=Password.getText().toString().trim();
        name=Name.getText().toString().trim();
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
//        if(TextUtils.isEmpty(name))
//        {
//            Toast.makeText(this,"Please enter a name",Toast.LENGTH_LONG).show();
//            return;
//        }
        if(password.length()<6)
        {
            Toast.makeText(this,"Password must be atleast 6 characters long",Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(RegisterActivity.this,"Welcome "+name,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}