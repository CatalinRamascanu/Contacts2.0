package com.example.ExpandableList;

/**
 * Created with IntelliJ IDEA.
 * User: cramascanu
 * Date: 9/26/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
import java.util.ArrayList;
import java.util.List;

public class Contact {

    public String name;
    public final List<String> children = new ArrayList<String>();

    public Contact(String name) {
        this.name = name;
    }

}
