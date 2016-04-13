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
import com.nkcsio.android.po.Company;
import com.nkcsio.android.po.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ZhangtaoActivity extends Activity {

	private TextView tvTitle,tvCompany;
	private Spinner spnBenweibi,spnZhidu;
	private ImageView ivBwbCheck,ivZhiduCheck;
	private Button btnSetup;
	private KuaijiApp kuaijiapp;
	private String company_name;
	private Handler handler,addHandler,addUCHandler;
	private boolean exist_flag = false;
	private boolean checkBwb_flag,checkZhidu_flag;
	private List<String> benweibiList;
	private ArrayAdapter<String> arrayAdapterBwb;
	private String selectedBenweibi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zhangtao);
		
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		this.tvCompany = (TextView) findViewById(R.id.tvCompany);
		this.spnBenweibi = (Spinner) findViewById(R.id.spnBwb);
		this.spnZhidu = (Spinner) findViewById(R.id.spnZhidu);
		this.ivBwbCheck = (ImageView) findViewById(R.id.ivBwbCheck);
		this.ivZhiduCheck = (ImageView) findViewById(R.id.ivZhiduCheck);
		this.btnSetup = (Button) findViewById(R.id.btnSetup);
		
		this.benweibiList = new ArrayList<String>();
		this.benweibiList.add("--请选择本位币--");
		this.benweibiList.add("人民币");
		this.benweibiList.add("美元");
		arrayAdapterBwb = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, benweibiList);
		this.spnBenweibi.setAdapter(arrayAdapterBwb);
		this.spnBenweibi.setSelection(0);
		
		this.spnZhidu.setSelection(0);
		
		this.handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(msg.what == 1)
				{//公司名存在
					exist_flag = true;
					tvTitle.setText("您的公司已经选择以下账套制度");
					
					String gsonCompany = (String)msg.obj;
					Gson gson = new Gson();
					Company company = gson.fromJson(gsonCompany, Company.class);
					kuaijiapp.company = company;

					String benweibi = company.getBenweibi();
					//Toast.makeText(getApplicationContext(), benweibi, Toast.LENGTH_SHORT).show();
					int pos = 0;
					for(String opt : benweibiList)
					{
						if(opt.equals(benweibi))
						{
							spnBenweibi.setSelection(pos);
						}
						pos++;
						
					}
					spnZhidu.setSelection(1);
					
					btnSetup.setText("继续");
				}

			}
		};
		
		this.addHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				if(msg.what == 1)
				{
					kuaijiapp.user = kuaijiapp.regUser;
					Intent intent=new Intent();
					intent.setClass(ZhangtaoActivity.this, FunctionActivity.class);
					startActivity(intent);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "插入失败", Toast.LENGTH_SHORT).show();
				}
			}
		};
		this.addUCHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				if(msg.what == 1)
				{
					kuaijiapp.user = kuaijiapp.regUser;
					Intent intent=new Intent();
					intent.setClass(ZhangtaoActivity.this, FunctionActivity.class);
					startActivity(intent);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "插入失败", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		this.kuaijiapp = (KuaijiApp) getApplication();
		User user = kuaijiapp.regUser;
		company_name = user.getCompanyname();
		this.tvCompany.setText(company_name);
		//检查公司名是否存在
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				doCheckCompany();
			}
		}).start();
		
		spnBenweibi.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectedBenweibi = spnBenweibi.getSelectedItem().toString();
				if(selectedBenweibi.contains("--"))
				{
					checkBwb_flag = false;
				}
				else
				{
					ivBwbCheck.setImageResource(R.drawable.reg_ok);
					checkBwb_flag = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spnZhidu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String selectedZhidu = spnZhidu.getSelectedItem().toString();
				if(selectedZhidu.contains("--"))
				{
					checkZhidu_flag = false;
				}
				else
				{
					ivZhiduCheck.setImageResource(R.drawable.reg_ok);
					checkZhidu_flag = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	protected void doCheckCompany() {
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("company_name", company_name));
		params.add(new BasicNameValuePair("option", "checkcompanyname"));
		
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
				 if(res.equals("Notexisted"))
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
	
	public void doSetup(View v)
	{
		if(!exist_flag)
		{
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doAddCompany();
				}
			}).start();
			
		}
		else
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doAddUserCompany();
				}
			}).start();
		}
		
			
	}

	protected void doAddUserCompany() {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		String strCompanyJson = gson.toJson(kuaijiapp.company);
		String StrUserJson = gson.toJson(kuaijiapp.regUser);
		
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("strCompanyJson", strCompanyJson));
		params.add(new BasicNameValuePair("StrUserJson", StrUserJson));
		params.add(new BasicNameValuePair("option", "addusercompany"));
		
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
				 if(res.equals("succeed"))
				 {
					 msg.what = 1;
					 addUCHandler.sendMessage(msg);
				 }
				 else
				 {
					 msg.what = -1;
					 addUCHandler.sendMessage(msg);
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

	protected void doAddCompany() {
		// TODO Auto-generated method stub
		Company company = new Company();
		company.setCompany_name(kuaijiapp.regUser.getCompanyname());
		company.setBenweibi(selectedBenweibi);
		Gson gson = new Gson();
		String strCompanyJson = gson.toJson(company);
		String StrUserJson = gson.toJson(kuaijiapp.regUser);
		
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("strCompanyJson", strCompanyJson));
		params.add(new BasicNameValuePair("StrUserJson", StrUserJson));
		params.add(new BasicNameValuePair("option", "addcompany"));
		
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
				 if(res.equals("succeed"))
				 {
					 msg.what = 1;
					 addHandler.sendMessage(msg);
				 }
				 else
				 {
					 msg.what = -1;
					 addHandler.sendMessage(msg);
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
		getMenuInflater().inflate(R.menu.zhangtao, menu);
		return true;
	}

}
