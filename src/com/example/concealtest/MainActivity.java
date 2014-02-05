package com.example.concealtest;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.MyKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

public class MainActivity extends Activity {

	private TextView outputContent;
	private EditText inputContent;
	private Crypto crypto;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//According to facebook sample, they use SharedPrefsBackedKeychain,
		//but it leads to unable to continue using saved data on SDCard if your app is uninstalled and newly installed
		//So planning to customize other keychain
		crypto = new Crypto(new MyKeyChain(this,"bmd1eWVudGllbmxvbmc="),
				new SystemNativeCryptoLibrary());
		if (!crypto.isAvailable()) {
			return;
		} else {
			Log.e("Crypto", "loaded");
		}
		initView();

	}

	private void initView() {
		inputContent = (EditText) findViewById(R.id.input);
		outputContent = (TextView) findViewById(R.id.gotContent);
		findViewById(R.id.encodeBt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				encode();
			}

		});
		findViewById(R.id.decodeBt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				decode();
			}

		});
		
		findViewById(R.id.testVideo).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,VideoTest.class));
			}
		});
	}

	private void encode() {
		try {
			String content=inputContent.getText().toString();
			File dest=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/concealTest");
			if(!dest.exists()) dest.mkdirs();
			
			File file = new File(dest, "test.txt");
			if(!file.exists()) file.createNewFile();
			OutputStream fileStream=new BufferedOutputStream(new FileOutputStream(file));
			
			OutputStream ouStream=crypto.getCipherOutputStream(fileStream, new Entity("test.txt"));
			ouStream.write(content.getBytes());
			ouStream.flush();
			ouStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void decode() {
		File dest=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/concealTest/test.txt");
		if(!dest.exists()) {
			Toast.makeText(this, "File is not avaiable", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			FileInputStream fileStream=new FileInputStream(dest);
			InputStream inputStream=crypto.getCipherInputStream(fileStream,new Entity("test.txt"));
			BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder builder=new StringBuilder();
			//must read entire stream to completion, otherwise wil cause security bugs
			String line;
			while((line=r.readLine())!=null){
				builder.append(line);
			}
			inputStream.close();
			outputContent.setText(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Decode error", Toast.LENGTH_SHORT).show();
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
