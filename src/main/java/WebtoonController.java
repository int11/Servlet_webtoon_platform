import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

//MultipartConfig: 뉴스 이미지 파일 업로드 처리, Servlet 3.0 API 부터 지원
@WebServlet("/webtoon.nhn")
@MultipartConfig(maxFileSize = 1024 * 1024 * 2, location = WebtoonDAO.Images_Server_Dir) // 2MB
public class WebtoonController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private WebtoonDAO dao;
	private ServletContext ctx;

	// 웹 리소스 기본 경로 지정
	private final String START_PAGE = "webtoonList.jsp";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = new WebtoonDAO();
		ctx = getServletContext();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		request.setCharacterEncoding("utf-8");

		String action = request.getParameter("action");
		Method m;
		String view = null;

		if (action == null) action = "listWebtoon";

		try {
			m = this.getClass().getMethod(action, HttpServletRequest.class);
			view = (String) m.invoke(this, request); // invoke(object, args), method 실행 m.invoke(NewsController객체, request 파라미터)
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			ctx.log("요청 action 없음!");
			request.setAttribute("error", "action파라미터가 잘못 되었습니다.!");
			view = START_PAGE;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// redirect
		if (view.startsWith("redirect:/")) { // length: 10
			String rview = view.substring("redirect:/".length()); // substring(beginIndex), redirect:/뒤부터 시작해서 문자열
			response.sendRedirect(rview);
		} 
		else {
			request.getRequestDispatcher(view).forward(request, response);
			// getServletContext().getRequestDispatcher(view).forward(request, response);
		}
	} // end of service method

	public String listWebtoon(HttpServletRequest request) {

		List<Webtoon> list;

		try {
			list = dao.getAll();
			
			request.setAttribute("webtoonlist", list);
		} catch (SQLException e) {
			e.printStackTrace();
			ctx.log("웹툰 목록 생성 과정에서 문제 발생!");
			request.setAttribute("error", "웹툰 목록이 정상적으로 처리되지 않았습니다!");
		}

		return "webtoonList.jsp";
	}

	/* 
	Part: This class represents a part or form item that was received within a multipart/form-data POST request.
	*/
	public String addWebtoon(HttpServletRequest request) {
		Webtoon n = new Webtoon();	

		try {
			System.out.println(n);
			BeanUtils.populate(n, request.getParameterMap()); // title, content
			System.out.println(n);
			Collection<Part> parts = request.getParts();
			
			for (Part part : parts) {
				// 바이너리 데이터만처리
				if (part.getHeader("Content-Disposition").contains("filename=")) {
					if (part.getName().equals("thumbnail")) {
						dao.setThumbnail(n, part);
					} else {
						dao.addImages(n, part);
					}
				}
			}
			
			dao.addWebtoon(n);

		} catch (Exception e) {
			e.printStackTrace();
			ctx.log("웹툰 추가 과정에서 문제 발생!!");
			request.setAttribute("error", "웹툰이 정상적으로 등록되지 않았습니다!!");
			return listWebtoon(request);
		}

		return "redirect:/webtoon.nhn?action=listWebtoon";
	}

	public String deleteWebtoon(HttpServletRequest request) {

		int aid = Integer.parseInt(request.getParameter("aid"));

		try {
			dao.delWebtoon(aid);
		} catch (SQLException e) {
			e.printStackTrace();
			ctx.log("웹툰 삭제 과정에서 문제 발생");
			request.setAttribute("error", "웹툰이 정상적으로 삭제되지 않았습니다!!");
			return listWebtoon(request);
		}

		return "redirect:/webtoon.nhn?action=listWebtoon";
	}

	public String getWebtoon(HttpServletRequest request) {

		int aid = Integer.parseInt(request.getParameter("aid"));

		Webtoon n;
		try {
			n = dao.getWebtoon(aid);
			request.setAttribute("webtoon", n);
		} catch (SQLException e) {
			e.printStackTrace();
			ctx.log("뉴스를 가져오는 과정에서 문제 발생!!");
			request.setAttribute("error", "뉴스를 정상적으로 가져오지 못했습니다!!");
		}

		return "webtoonView.jsp";
	}

}
