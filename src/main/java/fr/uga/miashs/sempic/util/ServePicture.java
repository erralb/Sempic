package fr.uga.miashs.sempic.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pierre Blarre <pierre.blarre@gmail.com>
 */
@WebServlet(name = "ServePicture", urlPatterns = {"/ServePicture/*"})
public class ServePicture extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        ServletContext context = getServletContext();

        String imgFolder = context.getInitParameter("imgFolder");
        String thumbFolder = context.getInitParameter("thumbFolder");

        String filename = imgFolder + request.getPathInfo();
        File image = new File(filename);
        if (!image.exists() || image.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }

        String width = request.getParameter("width");
        String height = request.getParameter("height");
        String crop = request.getParameter("crop");

        if (width != null || height != null) {
            String thumbFilename = thumbFolder + "/" + width + "x" + height + "-" + request.getPathInfo().substring(1);
            File thumb = new File(thumbFilename);
            if (!thumb.exists()) {

                // crop the file
                BufferedImage bimg = ImageIO.read(image);

                int intWidth = (width != null) ? Integer.parseInt(width) : bimg.getWidth();
                int intHeight = (int) (bimg.getHeight() * (((double) intWidth) / bimg.getWidth()));

                if (intWidth >= bimg.getWidth() || intHeight >= bimg.getHeight()) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The specified width or height are greater than the original image");
                    return;
                }

//                        int intHeight = (height != null) ?  Integer.parseInt(height) : bimg.getHeight();
//                        BufferedImage img = bimg.getSubimage(0, 0, intWidth, intHeight);
//                        BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
                Image img = bimg.getScaledInstance(intWidth, intHeight, Image.SCALE_FAST);
                BufferedImage copyOfImage = new BufferedImage(intWidth, intHeight, bimg.getType());

                Graphics g = copyOfImage.createGraphics();
                g.drawImage(img, 0, 0, null);
                g.dispose();

                thumb.getParentFile().mkdirs();
                thumb.createNewFile();
                ImageIO.write(copyOfImage, extension, thumb);

            }

            filename = thumbFilename;
        }

        // retrieve mimeType dynamically
        String mime = context.getMimeType(filename);
        if (mime == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setContentType(mime);
        File file = new File(filename);
        response.setContentLength((int) file.length());

        System.out.println(file);

        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
