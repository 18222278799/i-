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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.nkcsio.android.app.KuaijiApp;
import com.nkcsio.android.po.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    
	private EditText etCode,etPassword,etSurePassword,etIDcard,etName,etCompany,etAnswer;
	private Spinner spnJob,spnQuestion;
	private Button btnRegister;
	private ImageView ivCodeCheck,ivPasswordCheck,ivSurepasswordCheck,ivIDCheck,ivNameCheck,ivCompanyCheck,ivJobCheck,ivQuestionCheck,ivAnswerCheck;
	private boolean checkCode_flag,checkPassword_flag,checkSurepassword_flag,checkName_flag,checkIDcard_flag,checkCompany_flag,checkJob_flag,checkQuestion_flag,checkAnswer_flag;
	private String Code,Password,SurePassword,IDcard,Name,Company,Job,Question,Answer;
	private ProgressDialog loadPDialog;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		
		this.etCode=(EditText)findViewById(R.id.etCode);
		this.etPassword=(EditText)findViewById(R.id.etPassword);
		this.etSurePassword=(EditText)findViewById(R.id.etSurePassword);
		this.etName = (EditText) findViewById(R.id.etName);
		this.etIDcard = (EditText) findViewById(R.id.etIDcard);
		this.etCompany=(EditText)findViewById(R.id.etCompany);
		this.etAnswer=(EditText)findViewById(R.id.etAnswer);
		this.spnJob=(Spinner)findViewById(R.id.spnJob);
		this.spnQuestion=(Spinner)findViewById(R.id.spnQuestion);
		this.btnRegister=(Button)findViewById(R.id.btnRegister);
		
		this.ivCodeCheck = (ImageView) findViewById(R.id.ivCodeCheck);
		this.ivPasswordCheck = (ImageView) findViewById(R.id.ivPasswordCheck);
		this.ivSurepasswordCheck = (ImageView) findViewById(R.id.ivSurepasswordCheck);
		this.ivNameCheck = (ImageView) findViewById(R.id.ivNameCheck);
		this.ivIDCheck = (ImageView) findViewById(R.id.ivIDCheck);
		this.ivCompanyCheck = (ImageView) findViewById(R.id.ivCompanyCheck);
		this.ivJobCheck = (ImageView) findViewById(R.id.ivJobCheck);
		this.ivQuestionCheck = (ImageView) findViewById(R.id.ivQuestionCheck);
		this.ivAnswerCheck = (ImageView) findViewById(R.id.ivAnswerCheck);
		
		this.handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				loadPDialog.dismiss();
				if(msg.what == 1)
				{
					Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
					
					Gson gson = new Gson();
					User user = gson.fromJson((String)msg.obj, User.class);
					KuaijiApp kuaijiapp = (KuaijiApp) getApplication();
					kuaijiapp.regUser = user;
					
					Intent intent=new Intent();
					intent.setClass(RegisterActivity.this, ZhangtaoActivity.class);
					startActivity(intent);
					finish();
				}
				else if(msg.what == -1)
				{
					Toast.makeText(getApplicationContext(), "用户名已存在", Toast.LENGTH_SHORT).show();
					etCode.setText("");
					etPassword.setText("");
					etSurePassword.setText("");
				}
			}
		};
		
		this.loadPDialog = new ProgressDialog(this);
		this.loadPDialog.setMessage("正在注册，请稍候。。。");
		
/*
 * 检查输入结果
 */
		//检查账号
		this.etCode.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Code = arg0.toString();
				if(Code.length() >= 3)
				{
					ivCodeCheck.setImageResource(R.drawable.reg_ok);
					checkCode_flag = true;
				}
				else
				{
					ivCodeCheck.setImageResource(R.drawable.reg_fault);
					checkCode_flag = false;
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});	
		this.etCode.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkCode_flag)
					{
						Toast.makeText(getApplicationContext(), "账号字符数应不小于3", Toast.LENGTH_SHORT).show();						
					}
						
				}
			}
		});
		//检查密码
        this.etPassword.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Password = arg0.toString();
				if(Password.length() >= 6)
				{
					ivPasswordCheck.setImageResource(R.drawable.reg_ok);
					checkPassword_flag = true;
					etSurePassword.setEnabled(true);
				}
				else
				{
					ivPasswordCheck.setImageResource(R.drawable.reg_fault);
					checkPassword_flag = false;
					etSurePassword.setEnabled(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});	
		this.etPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkPassword_flag)
					{
						Toast.makeText(getApplicationContext(), "密码字符数应不小于6", Toast.LENGTH_SHORT).show();						
					}
				}
				
			}
		});
		
		//检查确认密码
        this.etSurePassword.addTextChangedListener(new TextWatcher() {		
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				SurePassword = arg0.toString();				
				if(!SurePassword.equals(etPassword.getText().toString()) || SurePassword.length() < 1)
				{
					ivSurepasswordCheck.setImageResource(R.drawable.reg_fault);
					checkSurepassword_flag = false;
				}						
				else 
				{
					checkSurepassword_flag = true;
					ivSurepasswordCheck.setImageResource(R.drawable.reg_ok);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});	
		this.etSurePassword.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkSurepassword_flag)
					{
						Toast.makeText(getApplicationContext(), "请确认输入了相同的密码", Toast.LENGTH_SHORT).show();
					}
				}
				
			}
		});
		
		
		//检查姓名
        this.etName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Name = arg0.toString();
				if(Name.length() > 1)
				{
					ivNameCheck.setImageResource(R.drawable.reg_ok);
					checkName_flag = true;
				}
				else
				{
					ivNameCheck.setImageResource(R.drawable.reg_fault);
					checkName_flag = false;
				}
					
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});	
		this.etName.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkName_flag)
					{
						Toast.makeText(getApplicationContext(), "请确认输入了真实姓名", Toast.LENGTH_SHORT).show();
						
					}						
					
				}
				
			}
		});
		
		//检查身份证号码
		this.etIDcard.addTextChangedListener(new TextWatcher() {
						
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				IDcard = arg0.toString();
				if(IDcard.length() != 18)
				{
					ivIDCheck.setImageResource(R.drawable.reg_fault);
					checkIDcard_flag = false;
				}
				else
				{
					String NumberIDcard = IDcard.substring(0, 16);
					if(NumberIDcard.contains("x") || NumberIDcard.contains("X"))
					{						
						ivIDCheck.setImageResource(R.drawable.reg_fault);
						checkIDcard_flag = false;
					}
					else
					{
						ivIDCheck.setImageResource(R.drawable.reg_ok);
						checkIDcard_flag = true;
					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        this.etIDcard.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkIDcard_flag)
					    Toast.makeText(getApplicationContext(), "请确认输入了正确形式的身份证号", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
      //检查公司名
        this.etCompany.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Company = arg0.toString();
				if(Company.length() > 1)
				{
					ivCompanyCheck.setImageResource(R.drawable.reg_ok);
					checkCompany_flag = true;
				}
				else
				{
					ivCompanyCheck.setImageResource(R.drawable.reg_fault);
					checkCompany_flag = false;
				}
					
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});	
		this.etCompany.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkCompany_flag)
					{
						Toast.makeText(getApplicationContext(), "请确认输入了公司名称", Toast.LENGTH_SHORT).show();
						
					}						
					
				}
				
			}
		});
		
		//检测职务
		spnJob.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Job = spnJob.getSelectedItem().toString().substring(0, 2);
				if(Job.contains("-"))
				{
					checkJob_flag = false;
				}
				else
				{
					ivJobCheck.setImageResource(R.drawable.reg_ok);
					checkJob_flag = true;
				}
				//Toast.makeText(getApplicationContext(), "checkJob_flag"+checkJob_flag, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//检测问题
		spnQuestion.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Question = spnQuestion.getSelectedItem().toString();
				if(Question.contains("--"))
				{
					checkQuestion_flag = false;
				}
				else
				{
					ivQuestionCheck.setImageResource(R.drawable.reg_ok);
					checkQuestion_flag = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//检测答案
        this.etAnswer.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Answer = arg0.toString();
				if(Answer.length() > 1)
				{
					ivAnswerCheck.setImageResource(R.drawable.reg_ok);
					checkAnswer_flag = true;
				}
				else
				{
					checkAnswer_flag = false;
					ivAnswerCheck.setImageResource(R.drawable.reg_fault);
				}
					
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});	
		this.etAnswer.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(!arg1)
				{
					if(!checkAnswer_flag)
					{
						Toast.makeText(getApplicationContext(), "请确认输入了答案", Toast.LENGTH_SHORT).show();
						
					}						
				}
				
			}
		});
		
	}

	public void doRegister(View v){

		boolean check_flag = checkCode_flag && checkPassword_flag && checkSurepassword_flag && checkName_flag && checkIDcard_flag && checkCompany_flag && checkJob_flag && checkQuestion_flag && checkAnswer_flag;
		loadPDialog.show();
		if(!check_flag)
		{
			Toast.makeText(getApplicationContext(), "请确认以上信息输入完整", Toast.LENGTH_SHORT).show();
		}
		else
		{			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doRegisterRequest();
				}
			}).start();
			
		}
	}
	
	private boolean doRegisterRequest()
	{
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> paramUser = new ArrayList<NameValuePair>();
		User user = new User();
		user.setUsername(Code);
		user.setPassword(Password);
		user.setReal_username(Name);
		user.setIdentity_number(IDcard);
		user.setCompanyname(Company);
		user.setJob(Job);
		user.setQuestion(Question);
		user.setAnswer(Answer);
		
		Gson gson = new Gson();
		String strUserJson = gson.toJson(user);
		paramUser.add(new BasicNameValuePair("strUserJson", strUserJson));
		paramUser.add(new BasicNameValuePair("option", "adduser"));
		
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
				 if(res.equals("existed"))
				 {
					 msg.what = -1;
					 handler.sendMessage(msg);
				 }
				 else if(res.equals("fail"))
				 {
					 msg.what = -2;
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
		
		return true;
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
