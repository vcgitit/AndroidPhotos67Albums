package com.vcpb.androidphotos67albums;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Victor Chen and Paul Barbaro on 4/23/2017.
 */

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filename;
    private Calendar captureDate;
    private Integer numTags;
    private Integer capacity;
    private Tag[] tags = null;
    private String caption;

    /**
     * Construct
     * @param n photo's filename
     * @param c initial caption
     * @param cd capture date and time
     * @param tag initial tag
     */
    public Photo(String n, String c, Calendar cd, Tag tag) {
        filename = n;
        caption = c;

        captureDate = cd;
        capacity = 40;
        numTags = 0;

        tags = new Tag[capacity];
        for (int i = 0; i < capacity; i++) {
            tags[i] = null;
        }

        addTag(tag);
    }

    /**
     * Add a tag to a photo
     * @param tag the tag to be added
     */
    public void addTag(Tag tag) {
        // check if this is duplicate tag
        for (int i = 0; i < numTags; i++) {
            if (tags[i].getName().compareToIgnoreCase(tag.getName()) == 0 && tags[i].getValue().compareToIgnoreCase(tag.getValue()) == 0) {
                return;
            }
        }

        tags[numTags] = tag;
        numTags++;
    }


    /**
     * Get Tag by Name
     * @param name the tag's name
     * @param value, the tag value
     * @return the tag with name matches tagName
     */
    public Tag getTagByName(String name, String value) {
        for (int i = 0; i < numTags; i++) {
            if (tags[i].getName().compareToIgnoreCase(name) == 0 && tags[i].getValue().compareToIgnoreCase(value) == 0) {
                return tags[i];
            }
        }
        return null;
    }


    /**
     * Delete Tag by its composition name
     * @param name, the tag name
     * @param value, the tag value
     * @return true if the tag is deleted, false otherwise
     */
    public boolean deleteTag(String name, String value) {
        for (int i = 0; i < numTags; i++) {
            if (tags[i].getName().compareToIgnoreCase(name) == 0 && tags[i].getValue().compareToIgnoreCase(value) == 0) {
                for (int j = i; j < numTags; j++) {
                    tags[j] = tags[j+1];
                }
                numTags--;

                return true;
            }
        }
        return false;
    }

    /**
     * Return tag(s) of the photo
     * @return the array of the tags of the photo
     */
    public Tag[] getTags() {
        return tags;
    }


    /**
     * Set the new caption to the photo
     * @param newc the new caption
     */
    public void setCaption(String newc) {
        caption = newc;
    }


    /**
     * Get the caption for the photo
     * @return caption
     */
    public String getCaption() {
        return caption;
    }


    /**
     * Get the date for the photo
     * @return captureDate
     */
    public Calendar getCapturedDate() {
        return captureDate;
    }


    /**
     * Get the file name for the photo
     * @return filename
     */
    public String getFileName() {
        return filename;
    }


    /**
     * Return the string representation of the Photo
     * @return the string representation of the Photo
     */
    public String toString() {
        return "(" + filename + ", " + caption + ", " + captureDate + ")";
    }

}
