/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package researchproject.models;

/**
 *
 * @author Iskandar
 */
public class Triple {
    public String subject;
    public String predicate;
    public String object;

 
    public Triple(String subject, String predicate, String object)
    {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }
    
    public String getSubject() {
        return "<" +subject +">" ;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return "<" +predicate +">";
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return "<" +object  +">";
    }

    public void setObject(String object) {
        this.object = object;
    }
    
    
}
