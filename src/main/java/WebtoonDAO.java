import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Part;

public class WebtoonDAO {

	// DB Driver 설치 및 연결

	private final String JDBC_DRIVER = "org.h2.Driver";
	private final String JDBC_URL = "jdbc:h2:tcp://localhost/~/project/jwbook/test";
	public static final String Images_Server_Dir = "/home/injea/project/jwbook/Servlet_webtoon_platform/src/main/webapp/img";

	public Connection open() {
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("Class is not found");
		}
		try {
			conn = DriverManager.getConnection(JDBC_URL, "sa", "");
		} catch (SQLException e) {
			System.out.println("Connection is fail!");
		}

		return conn;

	}

	public void addWebtoon(Webtoon n) throws Exception {
		Connection conn = open();
		System.out.println(n);

		// aid, title, img, date (문자열로 처리), content
		// CURRENT_TIMESTAMP(): 2024-05-16 13:14:14.451523, H2 database의 내장 함수
		String sql = "INSERT INTO webtoon(title, thumbnail, images, date) VALUES(?, ?, ?, CURRENT_TIMESTAMP())";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		// Java7부터 사용 try-with-resource 기법 예외 발생시 해당 리소스를 자동으로 close함
		try (conn; pstmt) {		
			
			pstmt.setString(1, n.getTitle());
			pstmt.setString(2, n.getThumbnail());
			pstmt.setString(3, String.join(",", n.getImages()));

			pstmt.executeUpdate();
		}
	}

	public List<Webtoon> getAll() throws SQLException {

		Connection conn = open();
		List<Webtoon> webtoonList = new ArrayList<>();

		String sql = "SELECT aid, title, thumbnail, PARSEDATETIME(LEFT(date, 19), 'yyyy-MM-dd HH:mm:ss') AS cdate FROM webtoon";
				
		
		PreparedStatement pstmt = conn.prepareStatement(sql);

		ResultSet rs = pstmt.executeQuery();

		try (conn; pstmt; rs) {
			while (rs.next()) {

				Webtoon w = new Webtoon();

				w.setAid(rs.getInt("aid"));
				w.setTitle(rs.getString("title"));
				w.setThumbnail(rs.getString("thumbnail"));
				w.setDate(rs.getString("cdate"));

				webtoonList.add(w);
			}
			return webtoonList;
		}
	}

	
	//뉴스기사 세부 내용을 보여주는 메서드
	public Webtoon getWebtoon(int aid) throws SQLException {
		Connection conn = open();
	
		Webtoon w = new Webtoon();
		
		String sql = "select aid, title, thumbnail, images, PARSEDATETIME(LEFT(date, 19), 'yyyy-MM-dd HH:mm:ss') as cdate from webtoon where aid = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, aid);
		
		ResultSet rs = pstmt.executeQuery();
		
		rs.next();

		try(conn;pstmt; rs) {
			w.setAid(rs.getInt("aid"));
			w.setTitle(rs.getString("title"));
			w.setThumbnail(rs.getString("thumbnail"));

			String imgString = rs.getString("images");
			List<String> imgList = Arrays.asList(imgString.split(","));
			w.setImages(imgList);
			w.setDate(rs.getString("cdate"));
			
			System.out.println(w);
			return w;
		}
	}	


	public void delWebtoon(int aid) throws SQLException {
		Webtoon w = this.getWebtoon(aid);

		Connection conn = open();
		
		String sql = "delete from webtoon where aid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt) {
			pstmt.setInt(1, aid);
			
			if(pstmt.executeUpdate() == 0) throw new SQLException("DB에러");						
		}
		
		String directoryPath = this.Images_Server_Dir + "/" + w.getTitle();
		File directory = new File(directoryPath);
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			directory.delete();
		}
	}

	public void addImages(Webtoon n, Part img) throws IOException {
		String fileName = getFilename(img);
		if (fileName != null && !fileName.isEmpty()) {
			String directoryPath = this.Images_Server_Dir + "/" + n.getTitle();
			File directory = new File(directoryPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String path = directoryPath + "/" + fileName;
			img.write(path);
		}
		
		n.getImages().add(img.getSubmittedFileName());
	}

	public void setThumbnail(Webtoon n, Part img) throws IOException {
		String fileName = getFilename(img);
		
		if (fileName != null && !fileName.isEmpty()) {
			String directoryPath = this.Images_Server_Dir + "/" + n.getTitle();
			File directory = new File(directoryPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String path = directoryPath + "/" + fileName;
			img.write(path);		
		}
		
		n.setThumbnail(fileName);
	}

	private String getFilename(Part part) {		
		String fileName = null;

		String header = part.getHeader("content-disposition");

		int start = header.indexOf("filename");
		fileName = header.substring(start + 10, header.length() - 1);

		return fileName;
	}
}
