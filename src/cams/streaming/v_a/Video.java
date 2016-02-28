package cams.streaming.v_a;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Video
 */
@WebServlet("/video")
public class Video extends HttpServlet {
	private static final long serialVersionUID = 1L;	 
	private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Video() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String videoFilename = URLDecoder.decode("video.mp4", "UTF-8");
	    String videoPath = System.getProperty("user.dir");
		Path video = Paths.get(videoPath, videoFilename);
		
		int length = (int) Files.size(video);
	    int start = 0;
	    int end = length - 1;
	 
	    String range = request.getHeader("Range");
	    
	    String rangeValues = range.substring(range.indexOf("=") + 1, range.length());
	    String rangeSplit [] = rangeValues.split("-");
	    
	    start = rangeSplit[0].isEmpty() ? start : Integer.valueOf(rangeSplit[0]);
	    start = start < 0 ? 0 : start;
	    
	    end = rangeSplit.length == 1 ? end : Integer.valueOf(rangeSplit[1]);
	    end = end > length - 1 ? length - 1 : end;   
	 
	    System.out.println("range: " + range);
	    System.out.println("start: " + start);
	    System.out.println("end: " + end);
	    
	    response.reset();
	    response.setBufferSize(1024);
	    response.setDateHeader("Last-Modified", Files.getLastModifiedTime(video).toMillis());
	    response.setHeader("Content-Disposition", String.format("inline;filename=\"%s\"", videoFilename));
	    response.setHeader("Accept-Ranges", "bytes"); 	
	    response.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, length));
	    response.setDateHeader("Expires", System.currentTimeMillis() + EXPIRE_TIME);
	    response.setContentType(Files.probeContentType(video));
	    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
	 
	    BufferedInputStream input = new BufferedInputStream(new FileInputStream (video.toString()), 1024);
	    BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream(), 1024);
	    
	    byte [] buffer = new byte[1024];
	    int count;	    
	    while ((count = input.read(buffer)) > 0) {
	    	output.write(buffer, 0, count);
        }
	    
	    input.close();
	    output.close();
	    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
