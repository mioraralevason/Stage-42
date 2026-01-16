package com.example.servlet;

import com.example.dao.ChaussureDao;
import com.example.model.Chaussure;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AdminServlet extends HttpServlet {
    private ChaussureDao chaussureDao = new ChaussureDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/") || path.equals("/insert")) {
            // Afficher le formulaire d'insertion
            showInsertForm(request, response);
        } else if (path.equals("/list")) {
            // Afficher la liste
            showList(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (request.getPathInfo().equals("/insert")) {
            processInsert(request, response);
        }
    }
    
    private void showInsertForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("marques", chaussureDao.getAllMarques());
        request.setAttribute("categories", chaussureDao.getAllCategories());
        request.setAttribute("tailles", chaussureDao.getAllTailles());
        request.setAttribute("couleurs", chaussureDao.getAllCouleurs());
        
        request.getRequestDispatcher("/jsp/admin-insertion.jsp")
               .forward(request, response);
    }
    
    private void processInsert(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            Chaussure chaussure = new Chaussure();
            chaussure.setNom(request.getParameter("nom"));
            chaussure.setPrix(Double.parseDouble(request.getParameter("prix")));
            chaussure.setStock(Integer.parseInt(request.getParameter("stock")));
            chaussure.setMarqueId(Integer.parseInt(request.getParameter("marqueId")));
            chaussure.setCategorieId(Integer.parseInt(request.getParameter("categorieId")));
            chaussure.setTailleId(Integer.parseInt(request.getParameter("tailleId")));
            chaussure.setCouleurId(Integer.parseInt(request.getParameter("couleurId")));
            chaussure.setTypeId(1);
            
            boolean success = chaussureDao.save(chaussure);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/list");
            }
            
        } catch (Exception e) {
            showInsertForm(request, response);
        }
    }
    
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setAttribute("chaussures", chaussureDao.findAll());
        request.getRequestDispatcher("/jsp/admin-liste.jsp")
               .forward(request, response);
    }
}