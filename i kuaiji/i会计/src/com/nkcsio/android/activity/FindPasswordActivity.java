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
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPasswordActivity extends Activity {

	private EditText etCodeFind,etQuestionFind,etAnswerFind;
	private Button btnSubmit,btnSureUsername;
	private Handler handler;
	private ProgressDialog loadPDialog;
	private User checkUser = new User();
	private boolean checkUser_flag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_password);
		
		this.etCodeFind = (EditText) findViewById(R.id.etCodeFind);
		this.etQuestionFind = (EditText) findViewById(R.id.etQuestionFind);
		this.etAnswerFind = (EditText) findViewById(R.id.etAnswerFind);
		this.btnSubmit = (Button) findViewById(R.id.btnSubmit);
		this.btnSureUsername = (Button) findViewById(R.id.btnSureUsername);
		this.loadPDialog = new ProgressDialog(this);
		this.loadPDialog.setMessage("正在验证，请稍候。。。");
		
		this.handler = new Handler()
		{
			public void handleMessage(android.os.Message msg) {
				loadPDialog.dismiss();
				if(msg.what == 1)
				{
					checkUser_flag = true;
					Toast.makeText(getApplicationContext(), "用户名存在，请输入答案", Toast.LENGTH_SHORT).show();
					Gson gson = new Gson();
					checkUser = gson.fromJson((String)msg.obj, User.class);
					
					etQuestionFind.setText(checkUser.getQuestion());
					etCodeFind.setEnabled(false);
					etAnswerFind.setEnabled(true);
				}
				else if(msg.what == -1)
				{
					Toast.makeText(getApplicationContext(), "用户名不存在,请确认用户名输入正确", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
    //找回密码
    public void doFind(View v)
    {
		if(checkUser_flag)
		{
			String answer = etAnswerFind.getText().toString();
			if(answer.equals(checkUser.getAnswer()))
			{
				Toast.makeText(getApplicationContext(), "验证成功！您的密码是：" + checkUser.getPassword(), Toast.LENGTH_LONG).show();
				Intent intent=new Intent();
				intent.setClass(FindPasswordActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "验证失败", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent();
				intent.setClass(FindPasswordActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "请先确认账号", Toast.LENGTH_SHORT).show();
		}
	}
    
    //确认账号
    public void doSureUsername(View v)
    {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				doSearchUsername();
			}
		}).start();
    }
	protected void doSearchUsername() {
		// TODO Auto-generated method stub
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> paramUser = new ArrayList<NameValuePair>();
		
		paramUser.add(new BasicNameValuePair("username", etCodeFind.getText().toString()));
		paramUser.add(new BasicNameValuePair("option", "searchusername"));
		
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_password, menu);
		return true;
	}

}
