package com.nkcsio.android.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.nkcsio.android.po.ChaxunItem;
import com.nkcsio.android.po.Company;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class ChaxunpingzhengActivity extends Activity {

	private ImageView ivcxpzback;
	private EditText etCxpzdate;
	private Button btnCxpzdate;
	
	private DisplayMetrics displayMetrics;
	private TableLayout dictCxpzTable;
	private List<ChaxunItem> rowlist;
	private Handler handler;
	
	private ProgressDialog loadPDialog;
	private boolean first_in = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chaxunpingzheng);
		
		this.ivcxpzback = (ImageView) findViewById(R.id.ivcxpzback);
		this.etCxpzdate = (EditText) findViewById(R.id.etCxpzdate);
		this.btnCxpzdate = (Button) findViewById(R.id.btnCxpzdate);
		this.etCxpzdate.setText("2015-07");
		
		this.displayMetrics = new DisplayMetrics();
		(ChaxunpingzhengActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		this.dictCxpzTable = (TableLayout) findViewById(R.id.dictCxpzTable);
		
		this.loadPDialog = new ProgressDialog(this);
		this.loadPDialog.setMessage("正在查找，请稍候...");
		
		this.handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(msg.what == 1)
				{
					makeTableHead();
					rowlist = new ArrayList<ChaxunItem>();
					String strrowlist = (String) msg.obj;
					Gson gson = new Gson();
					
					Type type =  new TypeToken<List<ChaxunItem>>(){}.getType();
					rowlist = gson.fromJson(strrowlist, type);
					
					int pz_id_er = rowlist.get(0).getPz_id();
					List<ChaxunItem> pzrowlist = new ArrayList<ChaxunItem>();
					int i = 0;
					for(ChaxunItem chaxunitem : rowlist)
					{				
						if(pz_id_er == chaxunitem.getPz_id())
						{
							pzrowlist.add(chaxunitem);
						}
						else
						{
							getCxeachrow(pzrowlist);
							pzrowlist.clear();
							pz_id_er = chaxunitem.getPz_id();
							pzrowlist.add(chaxunitem);
						}
						
						if(i == rowlist.size() - 1)
						{
							getCxeachrow(pzrowlist);
						}
						
						i++;
						
						
					}
					loadPDialog.dismiss();
					
				}
				else
				{
					Toast.makeText(getApplicationContext(), "kong", Toast.LENGTH_LONG).show();
				}

			}
		};
		
		rowlist = new ArrayList<ChaxunItem>();
		dictCxpzTable.removeAllViews();
		loadPDialog.show();
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub			
				doFilloutCXTable();
			}
		}).start();
	}

	protected void doFilloutCXTable() {
		// TODO Auto-generated method stub
		KuaijiApp kuaijiApp = (KuaijiApp) getApplication();
		Company company = kuaijiApp.company;
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("strdate", etCxpzdate.getText().toString()));
		params.add(new BasicNameValuePair("strcompany_id", company.getCompany_id() + ""));
		params.add(new BasicNameValuePair("option", "getCX"));
		
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

	private void makeTableHead()
	{
		TableRow row1 = new TableRow(this);

		TextView tvdate = new TextView(this);
		tvdate.setText("日期");
		tvdate.setWidth(displayMetrics.widthPixels / 7);
		tvdate.setGravity(Gravity.CENTER);
		tvdate.setTextSize(10);
		tvdate.setTextColor(R.color.white);
		tvdate.setBackgroundColor(R.color.black);
		
		TextView tvpzid = new TextView(this);
		tvpzid.setText("凭证号");
		tvpzid.setWidth(displayMetrics.widthPixels / 14);
		tvpzid.setGravity(Gravity.CENTER);
		tvpzid.setTextSize(10);
		tvpzid.setTextColor(R.color.white);
		tvpzid.setBackgroundColor(R.color.black);
		
		TextView tvdes = new TextView(this);
		tvdes.setText("摘要");
		tvdes.setWidth(displayMetrics.widthPixels / 7);
		tvdes.setGravity(Gravity.CENTER);
		tvdes.setTextSize(10);
		tvdes.setTextColor(R.color.white);
		tvdes.setBackgroundColor(R.color.black);
		
		TextView tvsubject = new TextView(this);
		tvsubject.setText("科目");
		tvsubject.setWidth(displayMetrics.widthPixels / 7);
		tvsubject.setGravity(Gravity.CENTER);
		tvsubject.setTextSize(10);
		tvsubject.setTextColor(R.color.white);
		tvsubject.setBackgroundColor(R.color.black);
		
		TextView tvlend = new TextView(this);
		tvlend.setText("借方金额");
		tvlend.setWidth(displayMetrics.widthPixels / 7);
		tvlend.setGravity(Gravity.CENTER);
		tvlend.setTextSize(10);
		tvlend.setTextColor(R.color.white);
		tvlend.setBackgroundColor(R.color.black);
		
		TextView tvload = new TextView(this);
		tvload.setText("贷方金额");
		tvload.setWidth(displayMetrics.widthPixels / 7);
		tvload.setGravity(Gravity.CENTER);
		tvload.setTextSize(10);
		tvload.setTextColor(R.color.white);
		tvload.setBackgroundColor(R.color.black);
		
		TextView tvzhidan = new TextView(this);
		tvzhidan.setText("制单人");
		tvzhidan.setWidth(displayMetrics.widthPixels / 7);
		tvzhidan.setGravity(Gravity.CENTER);
		tvzhidan.setTextSize(10);
		tvzhidan.setTextColor(R.color.white);
		tvzhidan.setBackgroundColor(R.color.black);

		row1.addView(tvdate);
		row1.addView(tvpzid);
		row1.addView(tvdes);
		row1.addView(tvsubject);
		row1.addView(tvlend);
		row1.addView(tvload);
		row1.addView(tvzhidan);
		dictCxpzTable.addView(row1);
	}
	
	private void getCxeachrow(List<ChaxunItem> pzidrowlist)
	{
		int i = 0;
		for(ChaxunItem chaxunitem : pzidrowlist)
		{
			Date date = chaxunitem.getDate();
			int pz_id = chaxunitem.getPz_id();
			String description = chaxunitem.getDescription();
			String subject_name = chaxunitem.getSubject_name();
			String lend_or_load = chaxunitem.getLend_or_load();
			float amount = chaxunitem.getAmount();
			String made_by = chaxunitem.getMade_by();
			
			TableRow row = new TableRow(ChaxunpingzhengActivity.this);

			TextView tvdate = new TextView(ChaxunpingzhengActivity.this);
			TextView tvpzid = new TextView(ChaxunpingzhengActivity.this);
			TextView tvdes = new TextView(ChaxunpingzhengActivity.this);
			TextView tvsubject = new TextView(ChaxunpingzhengActivity.this);
			TextView tvlend = new TextView(ChaxunpingzhengActivity.this);
			TextView tvload = new TextView(ChaxunpingzhengActivity.this);
			TextView tvzhidan = new TextView(ChaxunpingzhengActivity.this);
			
			tvdate.setWidth(displayMetrics.widthPixels / 7);
			tvdate.setGravity(Gravity.CENTER);
			tvdate.setTextSize(10);
			
			tvpzid.setWidth(displayMetrics.widthPixels / 12);
			tvpzid.setGravity(Gravity.CENTER);
			tvpzid.setTextSize(10);
			
			tvdes.setWidth(displayMetrics.widthPixels / 9);
			tvdes.setGravity(Gravity.CENTER);
			tvdes.setTextSize(10);
			
			tvsubject.setWidth(displayMetrics.widthPixels / 7);
			tvsubject.setGravity(Gravity.CENTER);
			tvsubject.setTextSize(10);
			
			tvlend.setWidth(displayMetrics.widthPixels / 7);
			tvlend.setGravity(Gravity.CENTER);
			tvlend.setTextSize(10);
			
			tvload.setWidth(displayMetrics.widthPixels / 7);
			tvload.setGravity(Gravity.CENTER);
			tvload.setTextSize(10);
			
			tvzhidan.setWidth(displayMetrics.widthPixels / 7);
			tvzhidan.setGravity(Gravity.CENTER);
			tvzhidan.setTextSize(10);
			
			if(i == 0)
			{
				tvdate.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
				tvdate.setBackgroundResource(R.drawable.bg_city_search_bottom);
				tvpzid.setText(pz_id + "");
				tvpzid.setBackgroundResource(R.drawable.bg_city_search_bottom);
			}
			else if(i == pzidrowlist.size() - 1)
			{
				tvdate.setText("");
				tvdate.setBackgroundResource(R.drawable.bg_city_search_top);
				tvpzid.setText("");
				tvpzid.setBackgroundResource(R.drawable.bg_city_search_top);
			}
			else
			{
				tvdate.setText("");
				tvdate.setBackgroundResource(R.drawable.bg_city_search_middle);
				tvpzid.setText("");
				tvpzid.setBackgroundResource(R.drawable.bg_city_search_middle);
			}
							
			tvdes.setText(description);
			tvdes.setBackgroundResource(R.drawable.bg_city_search_normal);			
			
			tvsubject.setText(subject_name);			
			tvsubject.setBackgroundResource(R.drawable.bg_city_search_normal);			
			
			if(lend_or_load.equals("lend"))
			{
				tvlend.setText(amount + "");
			}
			else if(lend_or_load.equals("load"))
			{
				tvload.setText(amount + "");
			}		
			tvlend.setBackgroundResource(R.drawable.bg_city_search_normal);								
			tvload.setBackgroundResource(R.drawable.bg_city_search_normal);			
			
			tvzhidan.setText(made_by);			
			tvzhidan.setBackgroundResource(R.drawable.bg_city_search_normal);

			row.addView(tvdate);
			row.addView(tvpzid);
			row.addView(tvdes);
			row.addView(tvsubject);
			row.addView(tvlend);
			row.addView(tvload);
			row.addView(tvzhidan);
			dictCxpzTable.addView(row);
			
			i++;
		}
	}
	
	
	public void cxback(View v)
	{
		finish();
	}
	
	public void goFill(View v)
	{
		dictCxpzTable.removeAllViews();
		loadPDialog.show();
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				doFilloutCXTable();
			}
		}).start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chaxunpingzheng, menu);
		return true;
	}

}
