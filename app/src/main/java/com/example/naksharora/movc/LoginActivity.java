package com.example.naksharora.movc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final TextView RegisterLink=(TextView)findViewById(R.id.LinkToRegister);
        final EditText Email = (EditText) findViewById(R.id.etUserEmail);
        final EditText Password = (EditText) findViewById(R.id.etPassword);
        final Button LoginButton=(Button)findViewById(R.id.etButton);
        //This is : if a user hasn't registered before and There a user will register himself/herself
        RegisterLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent RegisterActivityIntent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(RegisterActivityIntent);
            }
        });
        //On clicking login button user will be redirected to a screen which will show his/her
        // contacts and he/she will select which contact he/she wants to contact to
        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String email=Email.getText().toString();
                final String StringPassword=Password.getText().toString();
                final String EncryptedPassword=Encryption(StringPassword);
                //new GetDataTask().execute("http://192.168.43.165:5000/login?email="+email+"&password="+EncryptedPassword);
            }
        });
    }
    public String Encryption(String md5)
    {
        try
        {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i)
            {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
        }
        return null;
    }
}
