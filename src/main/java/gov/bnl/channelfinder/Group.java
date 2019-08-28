/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.bnl.channelfinder;

/**
 *
 * @author iusmani
 */
public class Group {

    private String group;

    public Group() {
    }

    public Group(String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }
    
    public String setGroup(String group) {
        return this.group = group;
    }

    @Override
    public String toString() {
        return "Group= '" + this.group + " ";
    }
}
