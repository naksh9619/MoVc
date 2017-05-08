package com.example.naksharora.movc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Region;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity{
    TextView LinkToLogin;
    EditText Email,Password,ConfirmPassword,FirstName,LastName;
    Button RegisterButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirstName=(EditText) findViewById(R.id.etFirstName);
        LastName=(EditText) findViewById(R.id.etLastName);
        Email=(EditText)findViewById(R.id.etUserEmail);
        Password=(EditText)findViewById(R.id.etPassword);
        ConfirmPassword=(EditText)findViewById(R.id.etConfirmPassword);
        RegisterButton=(Button)findViewById(R.id.etRegisterButton);
        LinkToLogin=(TextView)findViewById(R.id.etLoginLink);
        LinkToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent LoginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if((Password.getText().toString().equals(ConfirmPassword.getText().toString()))){
                    new PostDataClass().execute("http://192.168.43.165:5000/register");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Passwords Do Not Match!!",Toast.LENGTH_LONG).show();
                    Intent BackToRegisterIntent=new Intent(RegisterActivity.this,RegisterActivity.class);
                    startActivity(BackToRegisterIntent);
                }
            }
        });
    }
    public String Encryption(String md5){
        try{
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i){
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        }
        catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
    class PostDataClass extends AsyncTask<String,Void,String>
    {
        final String EmailToSend=Email.getText().toString();
        final String PasswordToSend=Password.getText().toString();
        final String FirstNameToSend=FirstName.getText().toString();
        final String LastNameToSend=LastName.getText().toString();
        final String EncryptedPasswordToSend=Encryption(PasswordToSend);
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog=new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params){
            try{
                return PostData(params[0]);
            }
            catch(IOException ex){
                return "Network Error!";
            }
            catch (JSONException ex){
                return "Data Invalid !";
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(progressDialog!=null)
                progressDialog.dismiss();
            if(result.contains("true"))//If Registration is Successful
            {
                Toast.makeText(getApplicationContext(),"You Are Successfully Registered!!",Toast.LENGTH_LONG).show();
            }
            else//On Unsuccessful Registration
            {
                Toast.makeText(getApplicationContext(),"Try Again Later!",Toast.LENGTH_LONG).show();
                Intent BackToRegisterIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
                startActivity(BackToRegisterIntent);
            }
        }
        private String PostData(String urlPath) throws IOException,JSONException{
            StringBuilder result=new StringBuilder();
            BufferedReader bufferedReader=null;
            BufferedWriter bufferedWriter=null;
            StringBuilder builder;
            try{
                JSONObject dataToSend=new JSONObject();
                dataToSend.put("Email",EmailToSend);
                dataToSend.put("Password",EncryptedPasswordToSend);
                dataToSend.put("FirstName",FirstNameToSend);
                dataToSend.put("LastName",LastNameToSend);
                //building connection to the server
                URL url=new URL(urlPath);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();
                //Writing data to server
                OutputStream outputStream=urlConnection.getOutputStream();
                bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();
                //read data response from server
                InputStream inputStream=urlConnection.getInputStream();
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line=bufferedReader.readLine())!=null)
                {
                    result.append(line).append("\n");
                }
                builder=new StringBuilder();
                builder.append(urlConnection.getResponseCode());
            }
            finally{
                if(bufferedReader!=null)
                    bufferedReader.close();
                if(bufferedWriter!=null)
                    bufferedReader.close();
            }
            return result.toString();
        }
    }
}
