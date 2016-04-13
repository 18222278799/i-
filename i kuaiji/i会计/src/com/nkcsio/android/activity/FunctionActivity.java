package com.nkcsio.android.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
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
import com.google.gson.reflect.TypeToken;
import com.nkcsio.android.app.KuaijiApp;
import com.nkcsio.android.po.Company;
import com.nkcsio.android.po.MingxizhangEachrow;
import com.nkcsio.android.po.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
public class FunctionActivity extends Activity {

	private TextView tvHello;
	private Handler handler;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_function);
		
		this.tvHello=(TextView)findViewById(R.id.tvHello);
		this.handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1)
				{
					String strcompany = (String) msg.obj;
					Company company = new Company();
                    Gson gson = new Gson();
                    company = gson.fromJson(strcompany,Company.class);
                    KuaijiApp kuaijiApp = (KuaijiApp) getApplication();
                    User user = kuaijiApp.user;
            		kuaijiApp.company = company;
            		tvHello.setText("您好，" + user.getReal_username() + " (" + user.getUsername() + ") " 
        					+ "所在公司：" + user.getCompanyname() + company.getCompany_name());
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				dogetCompany();
				
			}
		}).start();
		tvHello.setText("您好！");
		
	}
	
	protected void dogetCompany() {
		KuaijiApp kuaijiApp = (KuaijiApp) getApplication();
		User user = kuaijiApp.user;
		
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("strcompanyname", user.getCompanyname()));
		params.add(new BasicNameValuePair("option", "getcompanyname"));
		
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(uri);
		
		try {
			// 给请求设置参数
			request.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			// 发送请求
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 提取响应内容
				 String res = EntityUtils.toString(response.getEntity(), "utf-8");
				 Message msg = new Message();
				 if(res.equals("null"))
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
	
    //录凭证
	public void goRecord(View v){
		Intent intent=new Intent();
    	intent.setClass(FunctionActivity.this, JizhangpingzhengActivity.class);
    	startActivity(intent);
		}
	
	//查凭证
	public void goInquire(View v){
		KuaijiApp kuaijiApp = (KuaijiApp) getApplication();
		User user = kuaijiApp.user;
		if(user.getJob().equals("会计"))
		{
		    Intent intent=new Intent();
    	    intent.setClass(FunctionActivity.this, ChaxunpingzhengActivity.class);
    	    startActivity(intent);
		}
		else if (user.getJob().equals("主管"))
		{
			Intent intent=new Intent();
    	    intent.setClass(FunctionActivity.this, ChaxunpingzhengshActivity.class);
    	    startActivity(intent);
		}
	}
	
	
    public void goReg(View v)
    {
    	KuaijiApp kuaijiApp = (KuaijiApp) getApplication();
    	kuaijiApp.user = null;
    	kuaijiApp.company = null;
		
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("user_id", 0);
		editor.putString("username", "");
		editor.putString("password", "");
		editor.putString("real_username", "");
		editor.putString("identity_number", "");
		editor.putString("companyname", "");
		editor.putString("job", "");
		editor.putString("question", "");
		editor.putString("answer", "");
		editor.commit();
		
		Intent intent = new Intent(FunctionActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
    }
    

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.function, menu);
		return true;
	}

}
