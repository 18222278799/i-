package com.nkcsio.android.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.nkcsio.android.app.KuaijiApp;
import com.nkcsio.android.po.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText etCodeLogin,etPasswordLogin;
	private ProgressDialog loadPDialog;
	private Handler handler,againhandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		this.etCodeLogin=(EditText)findViewById(R.id.etCodeLogin);
		this.etPasswordLogin=(EditText)findViewById(R.id.etPasswordLogin);
				
		this.handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				loadPDialog.dismiss();
				if(msg.what == 1)
				{
					Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
					Gson gson = new Gson();
					User user = gson.fromJson((String)msg.obj, User.class);
					KuaijiApp kuaijiapp = (KuaijiApp) getApplication();
					kuaijiapp.user = user;
					
					
					//自动登录
					SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putInt("user_id", user.getUser_id());
					editor.putString("username", user.getUsername());
					editor.putString("password", user.getPassword());
					editor.putString("real_username", user.getReal_username());
					editor.putString("identity_number", user.getIdentity_number());
					editor.putString("companyname", user.getCompanyname());
					editor.putString("job", user.getJob());
					editor.putString("question", user.getQuestion());
					editor.putString("answer", user.getAnswer());
					editor.commit();
					
					Intent intent=new Intent();
					intent.setClass(LoginActivity.this, FunctionActivity.class);
					startActivity(intent);
					finish();
				}
				else if(msg.what == -1)
				{
					Toast.makeText(getApplicationContext(), "用户名不存在或密码错误", Toast.LENGTH_SHORT).show();
				}
			}
		};

		this.loadPDialog = new ProgressDialog(this);
		this.loadPDialog.setMessage("正在登录，请稍候...");
	}
	
	//连续按两次返回键退出
	private long time1=0;
	@Override
	public void onBackPressed() {
		if((System.currentTimeMillis()-time1)>2000){
		    Toast.makeText(getApplicationContext(), "再次按下返回键退出", Toast.LENGTH_SHORT).show();
		    time1=System.currentTimeMillis();
		}else{
			finish();
			super.onBackPressed();
		}
	}
	
	//自动登录
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		if(sp != null)
		{
			if(sp.contains("username") && sp.contains("password"))
			{
				int user_id = sp.getInt("user_id", 0);
				String username = sp.getString("username", "");
				String password = sp.getString("password", "");
				String real_username = sp.getString("real_username", "");
				String identity_number = sp.getString("identity_number", "");
				String companyname = sp.getString("companyname", "");
				String job = sp.getString("job", "");
				String question = sp.getString("question", "");
				String answer = sp.getString("answer", "");
				if(!(user_id == 0) || !username.equals("") || !password.equals(""))
				{
					User user = new User();
					user.setUser_id(user_id);
					user.setUsername(username);
					user.setPassword(password);
					user.setReal_username(real_username);
					user.setIdentity_number(identity_number);
					user.setCompanyname(companyname);
					user.setJob(job);
					user.setQuestion(question);
					user.setAnswer(answer);
					KuaijiApp kuaijiapp = (KuaijiApp) getApplication();
					kuaijiapp.user = user;
					
					Intent intent=new Intent();
					intent.setClass(LoginActivity.this, FunctionActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}
	}

    //做登录操作
    public void doLogin(View v){
    	loadPDialog.show();
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				doLoginRequest();
			}
		}).start();
	}
    protected void doLoginRequest() {
    	String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> paramUser = new ArrayList<NameValuePair>();
		User user = new User();
		user.setUsername(etCodeLogin.getText().toString());
		user.setPassword(etPasswordLogin.getText().toString());
		
		Gson gson = new Gson();
		String strUserJson = gson.toJson(user);
		paramUser.add(new BasicNameValuePair("strUserJson", strUserJson));
		paramUser.add(new BasicNameValuePair("option", "login"));
		
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(uri);
		
		try {
			// 给请求设置参数
			request.setEntity(new UrlEncodedFormEntity(paramUser,HTTP.UTF_8));
			// 发送请求
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 提取响应内容
				 String res = EntityUtils.toString(response.getEntity(), "utf-8");
				 Message msg = new Message();
				 if(res.equals("Notexist"))
				 {
					 msg.what = -1;
					 handler.sendMessage(msg);
				 }
				 else
				 {
					 msg.what = 1;
					 msg.obj = res;
					 handler.sendMessage(msg);
				 }
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
	//转到注册页面
    public void goRegister(View v){
	    Intent intent=new Intent();
	    intent.setClass(LoginActivity.this, RegisterActivity.class);
	    startActivity(intent);
	}
    
    
    //转到找回密码页面
    public void goFindPassword(View v){
	    Intent intent=new Intent();
	    intent.setClass(LoginActivity.this, FindPasswordActivity.class);
	    startActivity(intent);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
