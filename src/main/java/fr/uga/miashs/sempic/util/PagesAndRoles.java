/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.util;

import fr.uga.miashs.sempic.model.SempicUser;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public enum PagesAndRoles {
    
    /*
     * Pages that are managed by the filter
     * If a page is not listed here (or listed but with no roles), then it is accesible by anyone
     * If a page is listed here with roles, then the user must have at least one of these roles to acess the page
     */
    login("/faces/login.xhtml" ),
    createUser("/faces/create-user.xhtml"),
    test("/faces/test.xhtml", Roles.USER);
    
    
    public String path;
    public Roles[] allowedRoles;

    PagesAndRoles(String page) {
        this.path=page;
        this.allowedRoles=null;
    }
    
    PagesAndRoles(String page, Roles... allowedRoles) {
        this.path=page;
        this.allowedRoles=allowedRoles;
    }
    
    private static Map<String,PagesAndRoles> idx;
    static {
        idx = new HashMap<>();
        for (PagesAndRoles p : PagesAndRoles.values()) {
            idx.put(p.path, p);
        }
    }
    public static PagesAndRoles fromPath(String path) {
        PagesAndRoles res =  idx.get(path);
        if (res==null) {
            throw new IllegalArgumentException(path+" is not in ");
        }
        return res;
    }
}
