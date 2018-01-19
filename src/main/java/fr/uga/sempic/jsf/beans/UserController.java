/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.sempic.jsf.beans;

import fr.uga.miashs.sempic.model.SempicUser;
import fr.uga.miashs.sempic.model.datalayer.*;
import fr.uga.miashs.sempic.util.Roles;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.*;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Named
@SessionScoped
public class UserController implements Serializable {

    private SempicUser selected;

    @Inject
    private AuthManager auth;

    @EJB
    private SempicUserFacade dao;

    public String create() {
        boolean createSuccessful = false;
        try {
            if (selected.getFirstname().equals("admin") && selected.getLastname().equals("admin")) {
                selected.setRoles(Collections.singleton(Roles.ADMIN));
            }
            createSuccessful = dao.createAndCheck(selected);
        } catch (EJBException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException vEx = (ConstraintViolationException) ex.getCause();
                vEx.getConstraintViolations().forEach(cv -> {
                    System.out.println(cv);
                });
                vEx.getConstraintViolations().forEach(cv -> {
                    FacesContext.getCurrentInstance().addMessage("validationError", new FacesMessage(cv.getMessage()));
                });

            }
        }
        if (createSuccessful) {
            return "/index.xhtml?faces-redirect=true";
        } else {
            return "/create-user.xhtml?faces-redirect=true&error=true";
        }
    }

    public SempicUser getSelected() {
        if (selected == null) {
            selected = new SempicUser();
        }
        return selected;
    }

    public void setSelected(SempicUser selected) {
        this.selected = selected;
    }

    public List<SempicUser> getUsers() {
        return dao.findAll();
    }
}
