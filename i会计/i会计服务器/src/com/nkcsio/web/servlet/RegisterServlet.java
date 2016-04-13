package com.nkcsio.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nkcsio.web.biz.RecordBiz;
import com.nkcsio.web.biz.UserBiz;
import com.nkcsio.web.biz.impl.RecordBizImpl;
import com.nkcsio.web.biz.impl.UserBizImpl;
import com.nkcsio.web.po.ChaxunItem;
import com.nkcsio.web.po.Company;
import com.nkcsio.web.po.Content;
import com.nkcsio.web.po.JPItem;
import com.nkcsio.web.po.MingxizhangEachrow;
import com.nkcsio.web.po.User;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		String option = "";
		option = request.getParameter("option");
		System.out.println(option);
		
		if(option == null)
		{
			//System.out.println(option);
		}
		else if(option.equals("adduser"))
		{
			String strUser = request.getParameter("strUserJson");
			System.out.println(strUser);
			Gson gson = new Gson();
			User user = gson.fromJson(strUser, User.class);
			UserBiz userBiz = new UserBizImpl();
			if(user != null)
			{
				if(userBiz.usernameCheck(user.getUsername()) == null)
				{
					if(userBiz.addUser(user))
					{
						User returnUser = userBiz.usernameCheck(user.getUsername());
						returnUser.setCompanyname(user.getCompanyname());
						Gson returnGson = new Gson();
						String returnStrJson = returnGson.toJson(returnUser);
						out.print(returnStrJson);
						
					}
					else
					{
						out.print("fail");
					}
				}
				else
				{
					out.print("existed");
				}
			}
		}
		
		else if(option.equals("checkcompanyname"))
		{
			String company_name = request.getParameter("company_name");
			UserBiz userBiz = new UserBizImpl();
			Company returnCompany = new Company();
			returnCompany = userBiz.companynameCheck(company_name);
			if(returnCompany != null)
			{				
				Gson returnGson = new Gson();
				String returnStrJson = returnGson.toJson(returnCompany);
				out.print(returnStrJson);
			}
			else
			{
				out.print("Notexisted");
			}
		}
		else if(option.equals("addcompany"))
		{
			String strCompanyJson = request.getParameter("strCompanyJson");
			String StrUserJson = request.getParameter("StrUserJson");
			Gson gson = new Gson();
			Company company = gson.fromJson(strCompanyJson, Company.class);
			User user = gson.fromJson(StrUserJson, User.class);
			UserBiz userBiz = new UserBizImpl();
			RecordBiz recordBiz = new RecordBizImpl();
			if(userBiz.addCompany(company))
			{
				
				Company percompany = userBiz.companynameCheck(company.getCompany_name());
				System.out.println(user.getUser_id() + "");
				System.out.println(percompany.getCompany_id() + "");
				if(userBiz.addUserCompany(user.getUser_id(), percompany.getCompany_id()) && recordBiz.addcompanysubject(percompany.getCompany_id()))
				{
					out.print("succeed");
				}
				else
				{
					out.print("addusercompanyfail");
				}
			}
			else
			{
				out.print("addcompanyfail");
			}
		}
		
		else if(option.equals("addusercompany"))
		{
			String strCompanyJson = request.getParameter("strCompanyJson");
			String StrUserJson = request.getParameter("StrUserJson");
			Gson gson = new Gson();
			Company company = gson.fromJson(strCompanyJson, Company.class);
			User user = gson.fromJson(StrUserJson, User.class);
			UserBiz userBiz = new UserBizImpl();
			
			System.out.println(user.getUser_id()+"");
			System.out.println(company.getCompany_id()+"");
			if(userBiz.addUserCompany(user.getUser_id(), company.getCompany_id()))
			{
				out.print("succeed");
			}
			else
			{
				out.print("addusercompanyfail");
			}
		}
		
		else if(option.equals("login"))
		{
			String strUser = request.getParameter("strUserJson");
			System.out.println(strUser);
			Gson gson = new Gson();
			User user = gson.fromJson(strUser, User.class);
			UserBiz userBiz = new UserBizImpl();
			
			if(user != null)
			{
//				System.out.println(user.getUsername());
//				System.out.println(user.getPassword());
				User returnUser = userBiz.selectUser(user.getUsername(), user.getPassword());
				if(returnUser != null)
				{	
					returnUser.setCompanyname(userBiz.selectCompanynameByUserid(returnUser.getUser_id()));
					// 转为Json字符串
					Gson returnGson = new Gson();
					String returnStrJson = returnGson.toJson(returnUser);
					out.print(returnStrJson);
				}
				else
				{
					out.print("Notexist");
				}
			}
		}
		
		else if(option.equals("loginagain"))
		{
			String strUser = request.getParameter("strUserJson");
			System.out.println(strUser);
			Gson gson = new Gson();
			User user = gson.fromJson(strUser, User.class);
			UserBiz userBiz = new UserBizImpl();
			
			if(user != null)
			{
//				System.out.println(user.getUsername());
//				System.out.println(user.getPassword());
				User returnUser = userBiz.selectUser(user.getUsername(), user.getPassword());
				if(returnUser != null)
				{	
					returnUser.setCompanyname(userBiz.selectCompanynameByUserid(returnUser.getUser_id()));
					// 转为Json字符串
					Gson returnGson = new Gson();
					String returnStrJson = returnGson.toJson(returnUser);
					out.print(returnStrJson);
				}
				else
				{
					out.print("Notexist");
				}
			}
		}
		
		else if(option.equals("searchusername"))
		{
			String username = request.getParameter("username");
			System.out.println(username);
			UserBiz userBiz = new UserBizImpl();
			User returnUser = userBiz.usernameCheck(username);
			if(returnUser == null)
			{
				out.print("Notexist");
			}
			else
			{
				Gson returnGson = new Gson();
				String returnStrJson = returnGson.toJson(returnUser);
				out.print(returnStrJson);
			}
		}
		
		else if(option.equals("getcompanyname"))
		{
			String company_name = request.getParameter("strcompanyname");
			System.out.println(company_name);
			UserBiz userBiz = new UserBizImpl();
			Company company = userBiz.companynameCheck(company_name);
			if(company == null)
			{
				out.print("null");
			}
			else
			{
				Gson returnGson = new Gson();
				String returnStrJson = returnGson.toJson(company);
				out.print(returnStrJson);
			}
		}
		
		else if(option.equals("fillinmxz"))
		{
			String date = request.getParameter("strdate");
			int company_id = Integer.parseInt(request.getParameter("Strcompany_id"));
			List<MingxizhangEachrow> mingxizhang = new ArrayList<MingxizhangEachrow>();
			RecordBiz recordBiz = new RecordBizImpl();
			mingxizhang  = recordBiz.filloutMXZ(date, company_id);
			if(mingxizhang == null)
			{
				out.print("null");
			}
			else
			{
				Gson returnGson = new Gson();
				String returnStrJson = returnGson.toJson(mingxizhang);
				System.out.println(returnStrJson);
				
				out.print(returnStrJson);
				for(MingxizhangEachrow mxzer : mingxizhang)
				{
					System.out.println(mxzer.getSubject_number() + "");
				}
			}
		}
		
		else if(option.equals("updatecs"))
		{
			String strtests = request.getParameter("strContent");
			ArrayList<Content> tests = new ArrayList<Content>();
			Gson gson = new Gson();		
			Type type =  new TypeToken<ArrayList<Content>>(){}.getType();
			tests = gson.fromJson(strtests, type);
			int company_id = Integer.parseInt(request.getParameter("strcompany_id"));
			
			RecordBiz recordBiz = new RecordBizImpl();
			for(Content content : tests)
			{
				int subject_number = Integer.parseInt(content.getContent1());
				if(content.getContent4() != null)
				{
					float mcontent = Float.parseFloat(content.getContent4());
					recordBiz.modifyCompanysubject(company_id, subject_number, mcontent);
					System.out.println(content.getContent2());
				}
			}
			out.print("succeed");
		}
		
		else if(option.equals("selectdatecs"))
		{
			String strtests = request.getParameter("strContent");
			ArrayList<Content> tests = new ArrayList<Content>();
			Gson gson = new Gson();		
			Type type =  new TypeToken<ArrayList<Content>>(){}.getType();
			tests = gson.fromJson(strtests, type);
			int company_id = Integer.parseInt(request.getParameter("strcompany_id"));
			
			RecordBiz recordBiz = new RecordBizImpl();
			ArrayList<Content> newtests = new ArrayList<Content>();
			newtests = recordBiz.findCompanysubject(tests, company_id);
			Gson returnGson = new Gson();
			String returnStrJson = returnGson.toJson(newtests);
			out.print(returnStrJson);
		}
		
		else if(option.equals("addpingzheng"))
		{		
			int company_id = Integer.parseInt(request.getParameter("strcompany_id"));
			String username = request.getParameter("strusername");
			int pz_id = Integer.parseInt(request.getParameter("pzid"));
			String strdate = request.getParameter("strdate");
			String strJLJson = request.getParameter("strJLJson");
			
			List<JPItem> JPList = new ArrayList<JPItem>();
			Gson gson = new Gson();		
			Type type =  new TypeToken<List<JPItem>>(){}.getType();
			JPList = gson.fromJson(strJLJson, type);
			
			RecordBiz recordBiz = new RecordBizImpl();
			if(recordBiz.checkpzid(pz_id, company_id, strdate))
			{
				float total_amount = 0;
				List<Integer> pzr_ids = new ArrayList<Integer>();
				for(JPItem jpitem : JPList)
				{
					if(jpitem.getMoneyCount() != 0)
					{
						total_amount += jpitem.getMoneyCount();
						if(jpitem.isJie())
						{
							int pzr_id = recordBiz.addpzr(jpitem.getStrZhaiyao(), "lend", (float) jpitem.getMoneyCount());
							pzr_ids.add(pzr_id);
						}
						else
						{
							int pzr_id = recordBiz.addpzr(jpitem.getStrZhaiyao(), "load", (float) jpitem.getMoneyCount());
							pzr_ids.add(pzr_id);
						}				
						
					}
				}
				recordBiz.addpz(pz_id, strdate, username, total_amount, company_id);
				for(int i = 0;i < pzr_ids.size(); i++)
				{
					int pzr_id = pzr_ids.get(i);
					recordBiz.addcollection(pzr_id, pz_id, strdate, company_id);
					recordBiz.addpzrsub(pzr_id, JPList.get(i).getIdKemu());
				}
				
			}
			else
			{
				out.print("existed");
			}
		}
		
		else if(option.equals("getCX"))
		{
			String date = request.getParameter("strdate");
			int company_id = Integer.parseInt(request.getParameter("strcompany_id"));
			
			List<ChaxunItem> cxs = new ArrayList<ChaxunItem>();
			RecordBiz recordBiz = new RecordBizImpl();
			
			cxs  = recordBiz.getChaxun(company_id, date);
			if(cxs == null)
			{
				out.print("null");
			}
			else
			{
				Gson returnGson = new Gson();
				String returnStrJson = returnGson.toJson(cxs);
				out.print(returnStrJson);
				System.out.println(returnStrJson);
			}
		}
		
		
	}

}
