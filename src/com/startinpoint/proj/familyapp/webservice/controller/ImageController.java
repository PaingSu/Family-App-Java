package com.startinpoint.proj.familyapp.webservice.controller;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


/**
 * 
 * Image Controller
 * @author nankhinmhwe
 *
 */
@Transactional
@Controller
public class ImageController {
	protected final Log logger = LogFactory.getLog(this.getClass());
		
	ServletContext context;
	
	@Autowired
	public void setContext(ServletContext context) {
	    this.context = context;
	}
	
	@Value("${application.folder_path}")
	private String folderPath;

	/**
	 * Download / Get image by file name 
	 * @param response
	 * @param request
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"/auth/{name:.+}"},method=RequestMethod.GET)
	public ModelAndView handleDownlaod(HttpServletResponse response,HttpServletRequest request,@PathVariable("name")String fileName) throws Exception{
		String filePath = folderPath+"/"+fileName;
				
		if(Files.exists(Paths.get(filePath))){
			File file = new File(filePath);
			byte[] fileContent = Files.readAllBytes(file.toPath());
			String contentType = context.getMimeType(fileName);
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment; filename="
	                + fileName);
			OutputStream out = response.getOutputStream();
			out.write(fileContent);
			out.close();
			response.flushBuffer();
		}	
		
		return null;
	}
	

}
