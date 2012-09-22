package edu.buffalo.cse.cse605;

import edu.buffalo.cse.cse605.FDListFine;

public class GuardedReference{

    private FDListFine.Element element;

    public GuardedReference(FDListFine.Element element){
        this.element = element;
    }

    public FDListFine.Element get(){
        synchronized(element){
            return element;
        }
    }

    public void set(FDListFine.Element newElement){
        synchronized(element){
            element = newElement;
        }
    }
}
