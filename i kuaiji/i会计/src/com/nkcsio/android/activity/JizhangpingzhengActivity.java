package com.nkcsio.android.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import com.nkcsio.android.myview.MoneyView;
import com.nkcsio.android.po.Company;
import com.nkcsio.android.po.JPItem;
import com.nkcsio.android.po.MingxizhangEachrow;
import com.nkcsio.android.po.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;

@SuppressLint("SimpleDateFormat")
public class JizhangpingzhengActivity extends Activity {
	public int ID;
	public Date date;
	public List<JPItem> jpList;
	private Handler handler;
	private ProgressDialog loadPDialog;

	// *****************************************************************
	// 表格框架
	class JPTableFrame extends TextView {
		public final static boolean VERTICAL = true;
		public final static boolean HORIZONTAL = false;

		public JPTableFrame(Context context, boolean dir) {
			super(context);
			setBackgroundColor(getResources().getColor(R.color.table_frame));
			if (dir == HORIZONTAL) {
				setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						getResources().getDimensionPixelSize(
								R.dimen.table_frame_width)));
			} else {
				setLayoutParams(new LayoutParams(getResources()
						.getDimensionPixelSize(R.dimen.table_frame_width),
						LayoutParams.MATCH_PARENT));
			}
		}

		public JPTableFrame(Context context) {
			this(context, VERTICAL);
		}
	}

	// 文字输入框
	class JPEditText extends EditText {

		public JPEditText(Context context) {
			super(context);
			setTextColor(getResources().getColor(R.color.table_text_color));
			setTextSize(getResources().getDimension(R.dimen.table_font_size2));
			setInputType(InputType.TYPE_CLASS_TEXT);
			setSelectAllOnFocus(true);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			if(!isFocused()){
			}else{
				Paint paint = new Paint();
				paint.setStyle(Style.FILL);
				paint.setColor(getResources().getColor(R.color.table_editable_color));
				canvas.drawRect(new Rect(0, 0, getWidth(),getHeight()), paint);
			}
			super.onDraw(canvas);
		}
	}

	// 会计科目扩展列表
	class JPExpandableListView extends ExpandableListView {

		public JPExpandableListView(Context context) {
			super(context);
			setBackgroundColor(getResources()
					.getColor(R.color.table_list_color));
			setGroupIndicator(null);
			setDivider(getResources().getDrawable(R.drawable.divider));
			setChildDivider(getResources().getDrawable(R.drawable.divider));
		}

	}

	// 会计科目列表项
	class Item {
		int id;
		String name;

		public Item(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return "" + id + " " + name;
		}
	}

	// 会计科目备选列表数据结构
	class ItemTree {
		Item item;
		ItemTree[] subtree;

		public ItemTree(Item item, ItemTree[] subtree) {
			this.item = item;
			this.subtree = subtree;
		}

		public ItemTree(Item item) {
			this.item = item;
			this.subtree = new ItemTree[0];
		}
	}

	// 会计科目部件数据结构
	class JPKemu {
		int id;
		EditText et;
		ListView lv;
		ExpandableListView elv;

		public JPKemu(Context context) {
			id = -1;
			et = new JPEditText(context);
			et.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					EditText e=(EditText)arg0;
					if (arg1&&e.getText().toString().equals("")) {
						elv.setVisibility(View.VISIBLE);
					} else {
						elv.setVisibility(View.GONE);
						lv.setVisibility(View.GONE);
					}
				}
			});
			et.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable arg0) {
					
					String str=arg0.toString();
					FindListAdapter fla=new FindListAdapter(lv, str, itemtree);
					if(fla.data.size()==0||str.equals("")){
						lv.setVisibility(View.GONE);
						elv.setVisibility(View.VISIBLE);
					}else{
						lv.setAdapter(fla);
						elv.setVisibility(View.GONE);
						lv.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					
				}

				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					id=-1;
				}
				
			});
			lv = new ListView(context);
			lv.setBackgroundColor(getResources()
					.getColor(R.color.table_list_color));

			lv.setAdapter(new FindListAdapter(lv, new String(),itemtree));
			lv.setDivider(getResources().getDrawable(R.drawable.divider));
			lv.setVisibility(View.GONE);
			elv = new JPExpandableListView(context);
			elv.setAdapter(new ExListAdapter(elv, itemtree));
			elv.setVisibility(View.GONE);
		}

		public class FindListAdapter extends BaseAdapter {
			List<Item> data;
			ListView parent;
			public FindListAdapter(ListView l, String str, ItemTree[] itemtree) {
				super();
				parent=l;
				data = new ArrayList<JizhangpingzhengActivity.Item>();
				findDataByTree(str, itemtree);
				parent.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						et.setText(data.get(arg2).toString());
						id=data.get(arg2).id;
						lv.setVisibility(View.GONE);
						elv.setVisibility(View.GONE);
					}
				});

			}

			private void findDataByTree(String str, ItemTree[] itemtree) {
				for (ItemTree it : itemtree) {
					if (it.subtree.length == 0) {
						String the=it.item.toString();
						if (the.contains(str)) {
							data.add(it.item);
						}
					} else {
						findDataByTree(str, it.subtree);
					}
				}

			}

			@Override
			public int getCount() {
				return data.size();
			}

			@Override
			public Object getItem(int arg0) {
				return data.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				TextView tv = new TextView(JizhangpingzhengActivity.this);
				tv.setMinHeight(getResources().getDimensionPixelSize(
						R.dimen.kemu_item_height));
				Drawable dr = getResources().getDrawable(R.drawable.o_guider);
				dr.setBounds(0, 0, dr.getMinimumWidth(), dr.getMinimumHeight());
				tv.setCompoundDrawables(dr, null, null, null);
				tv.setText("  " + data.get(arg0).toString());
				tv.setGravity(Gravity.CENTER_VERTICAL);
				arg1 = tv;
				return arg1;
			}

		}

		protected class ExListAdapter extends BaseExpandableListAdapter {
			ItemTree[] temptree;
			ExpandableListView parent;
			int index;

			class groupClickL implements OnGroupClickListener {

				@Override
				public boolean onGroupClick(ExpandableListView arg0, View arg1,
						int arg2, long arg3) {
					if (temptree[index == -1 ? arg2 : index].subtree.length == 0) {
						et.setText(temptree[index == -1 ? arg2 : index].item
								.toString());
						id = temptree[index == -1 ? arg2 : index].item.id;
						elv.setVisibility(View.GONE);
						lv.setVisibility(View.GONE);
					}
					return false;
				}

			}

			public ExListAdapter(ExpandableListView elv, ItemTree[] itemtree,
					int index) {
				super();
				this.index = index;
				temptree = itemtree;
				parent = elv;
				parent.setOnGroupClickListener(new groupClickL());

			}

			public ExListAdapter(ExpandableListView elv, ItemTree[] itemtree) {
				this(elv, itemtree, -1);
			}

			@Override
			public Object getChild(int arg0, int arg1) {
				return temptree[arg0].subtree[arg1].item;
			}

			@Override
			public long getChildId(int arg0, int arg1) {
				return arg1;
			}

			@Override
			public View getChildView(int arg0, int arg1, boolean arg2,
					View arg3, ViewGroup arg4) {
				LinearLayout ll = new LinearLayout(JizhangpingzhengActivity.this);
				ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				ll.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(JizhangpingzhengActivity.this);
				tv.setText("\t");
				ExpandableListView elv = new JPExpandableListView(
						JizhangpingzhengActivity.this);
				elv.setAdapter(new ExListAdapter(elv,
						temptree[index == -1 ? arg0 : index].subtree, arg1));
				ll.addView(tv);
				ll.addView(elv);
				arg3 = ll;
				return arg3;
			}

			@Override
			public int getChildrenCount(int arg0) {
				return temptree[index == -1 ? arg0 : index].subtree.length;
			}

			@Override
			public Object getGroup(int arg0) {
				return temptree[arg0].item;
			}

			@Override
			public int getGroupCount() {
				return index == -1 ? temptree.length : 1;
			}

			@Override
			public long getGroupId(int arg0) {
				return arg0;
			}

			@Override
			public View getGroupView(int arg0, boolean arg1, View arg2,
					ViewGroup arg3) {
				TextView tv = new TextView(JizhangpingzhengActivity.this);
				tv.setMinHeight(getResources().getDimensionPixelSize(
						R.dimen.kemu_item_height));
				Drawable dr = null;
				if (temptree[index == -1 ? arg0 : index].subtree.length == 0) {
					dr = getResources().getDrawable(R.drawable.o_guider);
				} else if (arg1) {
					dr = getResources().getDrawable(R.drawable.up_guider);
				} else {
					dr = getResources().getDrawable(R.drawable.down_guider);
				}
				dr.setBounds(0, 0, dr.getMinimumWidth(), dr.getMinimumHeight());
				tv.setCompoundDrawables(dr, null, null, null);
				tv.setText("  "
						+ temptree[index == -1 ? arg0 : index].item.toString());
				tv.setGravity(Gravity.CENTER_VERTICAL);
				arg2 = tv;
				if (index != -1) {
					if (!arg1) {
						parent.getLayoutParams().height = (temptree[index].subtree.length + 1)
								* getResources().getDimensionPixelSize(
										R.dimen.kemu_item_height);
					} else {
						parent.getLayoutParams().height = (1) * getResources()
								.getDimensionPixelSize(R.dimen.kemu_item_height);
					}
				}
				return arg2;
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}

			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				return true;
			}

		}

	}

	EditText[] zhaiyaos;
	JPKemu[] kemus;
	MoneyView[] jies;
	MoneyView[] dais;
	int n;
	EditText etPZID;
	EditText etDate;
	TableLayout tlJizhangPingzheng;
	Button btnBaocun;
	ItemTree[] itemtree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jizhangpingzheng);
		
		this.loadPDialog = new ProgressDialog(this);
		this.loadPDialog.setMessage("正在保存，请稍候...");
		
		this.handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				if(msg.what == 1)
				{
					Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();					
				}
				else
				{
					Toast.makeText(getApplicationContext(), "凭证号已经存在", Toast.LENGTH_LONG).show();
					
				}
				loadPDialog.dismiss();
				finish();  
		        Intent intent = new Intent(JizhangpingzhengActivity.this, JizhangpingzhengActivity.class);  
		        startActivity(intent);  

			}
		};
		init();
	}

	private void initItemTree() {
		itemtree = null;
		itemtree = new ItemTree[] {
				new ItemTree(new Item(1001, "库存现金")),
				new ItemTree(new Item(1002, "银行存款")),
				new ItemTree(new Item(1012, "其他货币资金")),
				new ItemTree(new Item(1101, "短期投资"), new ItemTree[] {
						new ItemTree(new Item(110101, "股票")),
						new ItemTree(new Item(110102, "债券")),
						new ItemTree(new Item(110103, "基金")),
						new ItemTree(new Item(110110, "其他")), }),
				new ItemTree(new Item(1121, "应收票据")),
				new ItemTree(new Item(1122, "应收账款")),
				new ItemTree(new Item(1123, "预付账款")),
				new ItemTree(new Item(1131, "应收股利")),
				new ItemTree(new Item(1132, "应收利息")),
				new ItemTree(new Item(1221, "其他应收款")),
				new ItemTree(new Item(1401, "材料采购")),
				new ItemTree(new Item(1402, "在途物资")),
				new ItemTree(new Item(1403, "原材料")),
				new ItemTree(new Item(1404, "材料成本差异")),
				new ItemTree(new Item(1405, "库存商品")),
				new ItemTree(new Item(1407, "商品进销差价")),
				new ItemTree(new Item(1408, "委托加工物资")),
				new ItemTree(new Item(1411, "周转材料")),
				new ItemTree(new Item(1421, "消耗性生物资产")),
				new ItemTree(new Item(1501, "长期债券投资"), new ItemTree[] {
						new ItemTree(new Item(150101, "债券投资")),
						new ItemTree(new Item(150102, "其他债权投资")), }),
				new ItemTree(new Item(1511, "长期股权投资"), new ItemTree[] {
						new ItemTree(new Item(151101, "股票投资")),
						new ItemTree(new Item(151102, "其他股权投资")), }),
				new ItemTree(new Item(1601, "固定资产")),
				new ItemTree(new Item(1602, "累计折旧")),
				new ItemTree(new Item(1604, "在建工程"), new ItemTree[] {
						new ItemTree(new Item(160401, "建筑工程")),
						new ItemTree(new Item(160402, "安装工程")),
						new ItemTree(new Item(160403, "技术改造工程")),
						new ItemTree(new Item(160404, "其他支出")), }),
				new ItemTree(new Item(1605, "工程物资")),
				new ItemTree(new Item(1606, "固定资产清理")),
				new ItemTree(new Item(1621, "生产性生物资产")),
				new ItemTree(new Item(1622, "生产性生物资产累计折旧")),
				new ItemTree(new Item(1701, "无形资产")),
				new ItemTree(new Item(1702, "累计摊销")),
				new ItemTree(new Item(1801, "长期待摊费用")),
				new ItemTree(new Item(1901, "待处理财产损溢")),
				new ItemTree(new Item(2001, "短期借款")),
				new ItemTree(new Item(2201, "应付票据")),
				new ItemTree(new Item(2202, "应付账款")),
				new ItemTree(new Item(2203, "预收账款")),
				new ItemTree(new Item(2211, "应付职工薪酬")),
				new ItemTree(new Item(2221, "应交税费"),
						new ItemTree[] {
								new ItemTree(new Item(222101, "应交增值税"),
										new ItemTree[] {
												new ItemTree(new Item(22210101,
														"进项税额")),
												new ItemTree(new Item(22210102,
														"已交税金")),
												new ItemTree(new Item(22210103,
														"减免税金")),
												new ItemTree(new Item(22210104,
														"出口抵减内销产品应纳税额")),
												new ItemTree(new Item(22210105,
														"转出未交增值税")),
												new ItemTree(new Item(22210106,
														"销项税额")),
												new ItemTree(new Item(22210107,
														"出口退税")),
												new ItemTree(new Item(22210108,
														"进项税额转出")),
												new ItemTree(new Item(22210109,
														"转出多交增值税")), }),
								new ItemTree(new Item(222102, "未交增值税")),
								new ItemTree(new Item(222103, "应交营业税")),
								new ItemTree(new Item(222104, "应交消费税")),
								new ItemTree(new Item(222105, "应交资源税")),
								new ItemTree(new Item(222106, "应交所得税")),
								new ItemTree(new Item(222107, "应交土地增值税")),
								new ItemTree(new Item(222108, "应交城市维护建设税")),
								new ItemTree(new Item(222109, "应交房产税")),
								new ItemTree(new Item(222110, "应交土地使用税")),
								new ItemTree(new Item(222111, "应交车船使用税")),
								new ItemTree(new Item(222112, "应交个人所得税")),
								new ItemTree(new Item(222113, "教育费附加")), }),
				new ItemTree(new Item(2231, "应付利息")),
				new ItemTree(new Item(2232, "应付利润")),
				new ItemTree(new Item(2241, "其他应付款")),
				new ItemTree(new Item(2401, "递延收益")),
				new ItemTree(new Item(2501, "长期借款")),
				new ItemTree(new Item(2701, "长期应付款")),
				new ItemTree(new Item(3001, "实收资本")),
				new ItemTree(new Item(3002, "资本公积"), new ItemTree[] {
						new ItemTree(new Item(300201, "资本溢价")),
						new ItemTree(new Item(300202, "接受捐赠非现金资产准备")),
						new ItemTree(new Item(300206, "外币资本折算")),
						new ItemTree(new Item(300207, "其他资本公积")), }),
				new ItemTree(new Item(3101, "盈余公积"), new ItemTree[] {
						new ItemTree(new Item(310101, "法定盈余公积")),
						new ItemTree(new Item(310102, "任意盈余公积")),
						new ItemTree(new Item(310103, "法定公益金")), }),
				new ItemTree(new Item(3103, "本年利润")),
				new ItemTree(new Item(3104, "利润分配"), new ItemTree[] {
						new ItemTree(new Item(310401, "其他转入")),
						new ItemTree(new Item(310402, "提取法定盈余公积")),
						new ItemTree(new Item(310403, "提取法定公益金")),
						new ItemTree(new Item(310409, "提取任意盈余公积")),
						new ItemTree(new Item(310410, "应付利润")),
						new ItemTree(new Item(310411, "转作资本的利润")),
						new ItemTree(new Item(310415, "未分配利润")), }),
				new ItemTree(new Item(4001, "生产资本"), new ItemTree[] {
						new ItemTree(new Item(400101, "基本生产成本")),
						new ItemTree(new Item(400102, "辅助生产成本")), }),
				new ItemTree(new Item(4101, "制造费用")),
				new ItemTree(new Item(4301, "研发支出")),
				new ItemTree(new Item(4401, "工程施工")),
				new ItemTree(new Item(4403, "机械工业")),
				new ItemTree(new Item(5001, "主营业务收入")),
				new ItemTree(new Item(5051, "其他业务收入")),
				new ItemTree(new Item(5111, "投资收益")),
				new ItemTree(new Item(5301, "营业外收入"), new ItemTree[] {
						new ItemTree(new Item(530101, "非流动资产处置净收益")),
						new ItemTree(new Item(530102, "政府补助")),
						new ItemTree(new Item(530103, "捐赠收益")),
						new ItemTree(new Item(530104, "盘盈收益")),
						new ItemTree(new Item(530105, "其他")), }),
				new ItemTree(new Item(5401, "主营业务成本")),
				new ItemTree(new Item(5402, "其他业务成本")),
				new ItemTree(new Item(5403, "营业税金及附加"), new ItemTree[] {
						new ItemTree(new Item(540301, "消费税")),
						new ItemTree(new Item(540302, "营业税")),
						new ItemTree(new Item(540303, "城市维护建设税")),
						new ItemTree(new Item(540304, "资源税")),
						new ItemTree(new Item(540305, "土地增值税")),
						new ItemTree(new Item(540306, "城镇土地使用税")),
						new ItemTree(new Item(540307, "房产税")),
						new ItemTree(new Item(540308, "车船税")),
						new ItemTree(new Item(540309, "印花税")),
						new ItemTree(new Item(540310, "教育费附加")),
						new ItemTree(new Item(540311, "矿产资源补偿费")),
						new ItemTree(new Item(540312, "排污费")), }),
				new ItemTree(new Item(5601, "销售费用"), new ItemTree[] {
						new ItemTree(new Item(560101, "办公用品")),
						new ItemTree(new Item(560102, "房租")),
						new ItemTree(new Item(560103, "物业管理费")),
						new ItemTree(new Item(560104, "水电费")),
						new ItemTree(new Item(560105, "交际应酬费")),
						new ItemTree(new Item(560106, "市内交通费")),
						new ItemTree(new Item(560107, "差旅费")),
						new ItemTree(new Item(560108, "补助费")),
						new ItemTree(new Item(560109, "通讯费")),
						new ItemTree(new Item(560110, "工资")),
						new ItemTree(new Item(560111, "佣金")),
						new ItemTree(new Item(560112, "保险金")),
						new ItemTree(new Item(560113, "福利费")),
						new ItemTree(new Item(560114, "累计折旧")),
						new ItemTree(new Item(560115, "商品维修费")),
						new ItemTree(new Item(560116, "广告和业务宣传费")),
						new ItemTree(new Item(560199, "其他")), }),
				new ItemTree(new Item(5602, "管理费用"), new ItemTree[] {
						new ItemTree(new Item(560201, "办公用品")),
						new ItemTree(new Item(560202, "房租")),
						new ItemTree(new Item(560203, "物业管理费")),
						new ItemTree(new Item(560204, "水电费")),
						new ItemTree(new Item(560205, "交际应酬费")),
						new ItemTree(new Item(560206, "市内交通费")),
						new ItemTree(new Item(560207, "差旅费")),
						new ItemTree(new Item(560208, "通讯费")),
						new ItemTree(new Item(560209, "工资")),
						new ItemTree(new Item(560210, "保险金")),
						new ItemTree(new Item(560211, "福利费")),
						new ItemTree(new Item(560212, "累计折旧")),
						new ItemTree(new Item(560213, "开办费")),
						new ItemTree(new Item(560214, "职工教育经费")),
						new ItemTree(new Item(560215, "研究费用")),
						new ItemTree(new Item(560299, "其他")), }),
				new ItemTree(new Item(5603, "财务费用"), new ItemTree[] {
						new ItemTree(new Item(560301, "汇兑损益")),
						new ItemTree(new Item(560302, "利息")),
						new ItemTree(new Item(560303, "手续费")),
						new ItemTree(new Item(560399, "其他")), }),
				new ItemTree(new Item(5711, "营业外支出"), new ItemTree[] {
						new ItemTree(new Item(571101, "存货盘亏损毁")),
						new ItemTree(new Item(571102, "非流动资产处置净损失")),
						new ItemTree(new Item(571103, "坏账损失")),
						new ItemTree(new Item(571104, "无法收回的长期债券投资损失")),
						new ItemTree(new Item(571105, "无法收回的长期股权投资")),
						new ItemTree(new Item(571106, "自然灾害等不可抗力造成的损失")),
						new ItemTree(new Item(571107, "税收滞纳金")),
						new ItemTree(new Item(571108, "罚金、罚款")),
						new ItemTree(new Item(571109, "捐赠支出")),
						new ItemTree(new Item(571110, "其他")), }),
				new ItemTree(new Item(5801, "所得税费用")),
				new ItemTree(new Item(6000, "以前年度损益调整")),

		};
	}

	private void init() {
		n = 4;
		jpList = new ArrayList<JPItem>();
		zhaiyaos = new EditText[n];
		kemus = new JPKemu[n];
		jies = new MoneyView[n + 1];
		dais = new MoneyView[n + 1];
		initItemTree();
		MoneyViewListener l = new MoneyViewListener();
		tlJizhangPingzheng = (TableLayout) findViewById(R.id.tlJizhangPingzheng);
		btnBaocun = (Button) findViewById(R.id.btnBaocun);
		etPZID = (EditText) findViewById(R.id.etPZID);
		etDate = (EditText) findViewById(R.id.etDate);
		for (int i = 0; i <= n; i++) {
			TableRow tr = new TableRow(getApplicationContext());
			tr.addView(new JPTableFrame(getApplicationContext()), 1,
					LayoutParams.MATCH_PARENT);
			if (i == n) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("合计：");
				tv.setTextSize(getResources().getDimension(
						R.dimen.table_font_size2));
				tv.setTextColor(getResources().getColor(
						R.color.table_text_color));
				//tv.setGravity(Gravity.CENTER);
				tr.addView(tv, new TableRow.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 2));
			} else {
				tr.addView(
						zhaiyaos[i] = new JPEditText(getApplicationContext()),
						new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT,
								1));
				tr.addView(new JPTableFrame(getApplicationContext()), 1,
						LayoutParams.MATCH_PARENT);
				kemus[i] = new JPKemu(getApplicationContext());
				tr.addView(kemus[i].et, new TableRow.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1));
			}
			tr.addView(new JPTableFrame(getApplicationContext()), 1,
					LayoutParams.MATCH_PARENT);
			tr.addView(jies[i] = new MoneyView(getApplicationContext()),
					new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			jies[i].setOnFocusChangeListener(l);
			tr.addView(new JPTableFrame(getApplicationContext()), 1,
					LayoutParams.MATCH_PARENT);
			tr.addView(dais[i] = new MoneyView(getApplicationContext()),
					new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			dais[i].setOnFocusChangeListener(l);
			tr.addView(new JPTableFrame(getApplicationContext()), 1,
					LayoutParams.MATCH_PARENT);
			tlJizhangPingzheng.addView(tr);
			tlJizhangPingzheng.addView(new JPTableFrame(
					getApplicationContext(), JPTableFrame.HORIZONTAL));
		}
		jies[n].setFocusable(false);
		dais[n].setFocusable(false);
		LinearLayout ll = (LinearLayout) findViewById(R.id.llKemu);
		for (int i = 0; i < n; i++) {
			ll.addView(kemus[i].elv);
			ll.addView(kemus[i].lv);
		}
	}

	class MoneyViewListener implements View.OnFocusChangeListener {
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			MoneyView mv = (MoneyView) arg0;
			if (!arg1) {
				if (mv.getValue()) {
					mv.setText(new DecimalFormat("#########.##")
							.format(mv.value));
				}

				for (int i = 0; i < n; i++) {
					if (mv.getValue()) {
						if (mv == jies[i]) {
							dais[i].setValue(-1);
						} else if (mv == dais[i]) {
							jies[i].setValue(-1);
						}
					}
				}

				double vjie = 0d, vdai = 0d;
				for (int i = 0; i < n; i++) {
					vjie += jies[i].value;
					vdai += dais[i].value;
				}
				jies[n].setValue(vjie);
				dais[n].setValue(vdai);
			}

		}

	}

	public void btnBaocunClick(View view) {
		if (etPZID.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "请输入凭证字号",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!getDate()) {
			Toast.makeText(getApplicationContext(), "请正确输入日期",
					Toast.LENGTH_SHORT).show();
			return;
		}
		for (int i = 0; i < n; i++) {
			if (!((!zhaiyaos[i].getText().toString().equals("")
					&& !kemus[i].et.getText().toString().equals("") && (!jies[i]
					.getText().toString().equals("") || !dais[i].getText()
					.toString().equals(""))) || (zhaiyaos[i].getText()
					.toString().equals("")
					&& kemus[i].et.getText().toString().equals("") && (jies[i]
					.getText().toString().equals("") && dais[i].getText()
					.toString().equals(""))))) {
				Toast.makeText(getApplicationContext(),
						"请将第" + (i + 1) + "行输入完整", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		for (int i = 0; i < n; i++) {
			if (kemus[i].id == -1
					&& !zhaiyaos[i].getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"请在第" + (i + 1) + "行输入正确的会计科目", Toast.LENGTH_SHORT)
						.show();
				return;
			}
		}
		jies[n].getValue();
		double vjie = jies[n].value;
		dais[n].getValue();
		double vdai = dais[n].value;
		if ((int) (vjie * 100) == (int) (vdai * 100) && (int) (vjie * 100) != 0) {
			
			
			ID = Integer.parseInt(etPZID.getText().toString());
			for (int i = 0; i < n; i++) {
				JPItem jpi = new JPItem();
				jpi.setStrZhaiyao(zhaiyaos[i].getText().toString());
				jpi.setIdKemu(kemus[i].id);
				jpi.setMoneyCount(jies[i].value > dais[i].value ? jies[i].value
						: dais[i].value);
				jpi.setJie_dai(jies[i].getValue() ? JPItem.JIE : JPItem.DAI);
				jpList.add(jpi);
			}
			
		    loadPDialog.show();
			new Thread (new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					giveData();
				}
			}).start();
			
		} else {
			Toast.makeText(getApplicationContext(), "录入借贷不平",
					Toast.LENGTH_SHORT).show();
		}
		// *****************************************************************
		
	}

	private boolean getDate() {
		try {
			String str = etDate.getText().toString();
			String y = "" + str.charAt(0) + str.charAt(1) + str.charAt(2)
					+ str.charAt(3);
			String m = "" + str.charAt(5) + str.charAt(6);
			String d = "" + str.charAt(8) + str.charAt(9);
			int year = Integer.parseInt(y);
			int month = Integer.parseInt(m);
			int day = Integer.parseInt(d);
			date = new Date(year - 1900, month-1, day);
			

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void giveData() {
		// 传递信息
		// public int ID; 凭证字号
		// public Date date; 日期
		// public List<JPItem> jpList; 列表项
		KuaijiApp kuaijiApp = (KuaijiApp) getApplication();
		Company company = kuaijiApp.company;
		User user = kuaijiApp.user;
		String strdate = etDate.getText().toString();
		Gson gson = new Gson();
		String strJLJson = gson.toJson(jpList);
		
		String uri = "http://10.0.2.2:8088/iAccountingServer/RegisterServlet";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("strcompany_id", company.getCompany_id() + ""));
		params.add(new BasicNameValuePair("strusername", user.getUsername()));
		params.add(new BasicNameValuePair("pzid", ID + ""));
		params.add(new BasicNameValuePair("strdate", strdate));
		params.add(new BasicNameValuePair("strJLJson", strJLJson));
		params.add(new BasicNameValuePair("option", "addpingzheng"));
		
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
				 if(res.equals("existed"))
				 {
					 msg.what = -1;
					 handler.sendMessage(msg);
				 }
				 else
				 {
					 msg.what = 1;
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
		getMenuInflater().inflate(R.menu.jizhangpingzheng, menu);
		return true;
	}

}
