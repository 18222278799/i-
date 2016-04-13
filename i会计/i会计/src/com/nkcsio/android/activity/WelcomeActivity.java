package com.nkcsio.android.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class WelcomeActivity extends Activity {

	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		
		this.handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				
				//SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
				if(msg.what==1){
					/*if (!sp.getBoolean("first", true)) {
						String username = sp.getString("username", "");
						Intent intent=new Intent();
						//跳转到自动登录成功界面
						intent.setClass(WelcomeActivity.this, LoginActivity.class);
						intent.putExtra("username", username);
						startActivity(intent);
						finish();
					}else{*/
					    Intent intent=new Intent();
					    intent.setClass(WelcomeActivity.this, LoginActivity.class);
					    startActivity(intent);
					    finish();
					//}
				}
			}
		};
		
		new Thread(new MyRun()).start();
	}

	private class MyRun implements Runnable{

		@Override
		public void run() {
			Message message=new Message();
			message.what=1;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handler.sendMessage(message);
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

}
