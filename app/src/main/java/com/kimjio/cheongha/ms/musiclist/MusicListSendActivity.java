package com.kimjio.cheongha.ms.musiclist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.kimjio.cheongha.ms.R;
import com.kimjio.cheongha.ms.tool.HiddenCode;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.Vector;

public class MusicListSendActivity extends AppCompatActivity {
    EditText mTitle, mMessage;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist_send);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        boolean isAdmin = getIntent().getBooleanExtra("userAdmin_1", false);
        if (!isAdmin)
            finish();

        mTitle = (EditText) findViewById(R.id.mTitle);
        mMessage = (EditText) findViewById(R.id.mMessage);
    }

    public void sendData(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicListSendActivity.this);
        builder.setTitle(R.string.post_musiclist_title);
        builder.setMessage(R.string.post_musiclist_alert);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = mTitle.getText().toString().trim();
                String message = mMessage.getText().toString().trim();

                if (!title.isEmpty() && !message.isEmpty()) {
                    (new HttpTask()).execute(title, message);
                }
            }
        });
        builder.show();
    }

    private class HttpTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(MusicListSendActivity.this);
            mDialog.setIndeterminate(true);
            mDialog.setMessage(getString(R.string.post_musiclist_posting));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpPost postRequest = new HttpPost("https://script.google.com/macros/s/AKfycbwR755X_mEWKZ8LKQuQf81t5rVerzOLCg1ztZyHisNr7rB8rIo/exec");


                //전달할 값들
                Vector<NameValuePair> nameValue = new Vector<>();
                nameValue.add(new BasicNameValuePair("sheet_name", "MusicList"));
                nameValue.add(new BasicNameValuePair("title", params[0]));
                nameValue.add(new BasicNameValuePair("message", params[1]));
                nameValue.add(new BasicNameValuePair("deviceId", Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)));

                nameValue.add(new BasicNameValuePair("code", HiddenCode.getHiddenCode()));

                //웹 접속 - UTF-8으로
                HttpEntity Entity = new UrlEncodedFormEntity(nameValue, "UTF-8");
                postRequest.setEntity(Entity);

                HttpClient mClient = new DefaultHttpClient();
                mClient.execute(postRequest);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);

            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }

            if (value) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicListSendActivity.this);
                builder.setTitle(R.string.post_musiclist_title);
                builder.setMessage(R.string.post_musiclist_success);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();

                mTitle.setText("");
                mMessage.setText("");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicListSendActivity.this);
                builder.setTitle(R.string.post_notice_title);
                builder.setMessage(R.string.post_notice_failed);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }
        }
    }

}
