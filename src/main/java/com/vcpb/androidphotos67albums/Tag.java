package com.vcpb.androidphotos67albums;

import java.io.Serializable;

/**
 * Created by Victor Chen and Paul Barbaro on 4/23/2017.
 */

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;

    /**
     * Construct the name and the value of the tag
     * @param n = name
     * @param v = value
     */
    public Tag(String n, String v) {
        name = n;
        value = v;
    }

    /**
     * Return the name of the tag
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the value of the tag
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the string representation of the tag
     * @return the string representation of the tag
     */
    public String toString() {
        return "(" + name + ", " + value + ")";
    }

}
