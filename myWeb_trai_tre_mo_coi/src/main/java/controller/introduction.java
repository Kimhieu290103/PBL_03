package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import database.ChildrenDAO;
import database.Introducter_user_DAO;
import database.IntroductionDAO;
import database.UserDAO;
import model.Children;
import model.Introducter_user;
import model.Introduction;
import model.User;

@WebServlet("/introduction")
public class introduction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public introduction() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("iName");
		String gender = request.getParameter("iGender");
		String dateTmp = request.getParameter("iDate");
		String health = request.getParameter("iHealth");
		String education = request.getParameter("iEducation");
		String reason = request.getParameter("iReason");
		
		String url = "";
		
		UserDAO usDAO= new UserDAO();
		Object obj = request.getSession().getAttribute("user");
		User user=null;
		user= (User)obj;
		
		boolean check;
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date utilDate = format.parse(dateTmp);
			java.sql.Date date = new java.sql.Date(utilDate.getTime());
			Part file = request.getPart("file-input");
			String image = file.getSubmittedFileName();
			String uploadPath = "D:/workspace_PBL/PBL3/myWeb_trai_tre_mo_coi/src/main/webapp/uploads/children/" + image;
			
			try {
				FileOutputStream fos = new FileOutputStream(uploadPath);
				InputStream is = file.getInputStream();
				byte[] data = new byte[is.available()];
				is.read(data);
				fos.write(data);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ChildrenDAO childrenDAO = new ChildrenDAO();
			long time = System.currentTimeMillis();
			String id = "C" + String.valueOf(time).substring(0,8);
			Children children = new Children(id, name, date, gender, reason, health, education, 0, image);
			childrenDAO.insert(children);
			if(user!=null) {
				ChildrenDAO chDAO= new ChildrenDAO();
				Children ch= new Children();
				ch = chDAO.selectById(name);
				Introducter_user_DAO in_us_DAO = new Introducter_user_DAO();
				Introducter_user in_us = new Introducter_user();
				in_us= in_us_DAO.selectByIdUser(user);
				if(in_us== null) {
					request.setAttribute("error", "Bạn chưa đăng kí trở thành người giới thiệu!");
					url="/introduction.jsp";
				}else {
				 LocalDate localDate = LocalDate.now(); // Lấy ngày hiện tại
			        Date currentDate = Date.valueOf(localDate);
			        Random rand = new Random();
			         int ranNum = rand.nextInt(1000)+1;
			        String s=Integer.toString(ranNum);
					String id1 ="IN"+s;
					IntroductionDAO inDAO= new IntroductionDAO();
					Introduction in = new Introduction(id1,ch,in_us.getIntroducter(),0,currentDate,reason);
					inDAO.insert(in);
					if(in!=null) check = true; else check= false;
					if (check) {
					    request.setAttribute("SuccessIntroduction", true);
					} else {
					    request.setAttribute("SuccessIntroduction", false);
					}
					url="/introduction.jsp";	
				}
			}else {
				request.setAttribute("error", "Bạn chưa đăng nhập!");
				url="/introduction.jsp";
			}
			url = "/children_manage.jsp";
		} catch (Exception e1) {
			e1.printStackTrace();
			url = "/children_manage.jsp";
		}
		RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}