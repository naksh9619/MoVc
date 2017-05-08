package com.example.naksharora.movc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final TextView RegisterLink=(TextView)findViewById(R.id.LinkToRegister);
        final EditText Email=(EditText) findViewById(R.id.etUserEmail);
        final EditText Password=(EditText) findViewById(R.id.etPassword);
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
                new GetDataClass().execute("http://192.168.43.165:5000/login?email="+email+"&password="+StringPassword);
            }
        });
    }
    class GetDataClass extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog=new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please Wait!!");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params){
            try{
                return GetData(params[0]);
            }catch(IOException ex){
                return "Network Error!!";
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(progressDialog!=null)
                progressDialog.dismiss();
            if(result.contains("true")) {
                Toast.makeText(LoginActivity.this,"Login Successfull!!",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(LoginActivity.this,"Login UnSuccessfull!!",Toast.LENGTH_LONG).show();
            }
        }
        private String GetData(String urlpath) throws IOException{
            StringBuilder result=new StringBuilder();
            BufferedReader bufferedReader=null;
            StringBuilder builder;
            try{
                URL url=new URL(urlpath);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                InputStream inputStream=urlConnection.getInputStream();
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line=bufferedReader.readLine())!=null) {
                    result.append(line).append("\n");
                }
                builder = new StringBuilder();
                builder.append(urlConnection.getResponseCode());
            }finally{
                if(bufferedReader!=null)
                    bufferedReader.close();
            }
            return result.toString();
        }
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
}
