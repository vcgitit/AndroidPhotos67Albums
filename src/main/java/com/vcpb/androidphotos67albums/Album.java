package com.vcpb.androidphotos67albums;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Victor Chen and Paul Barbaro on 4/23/2017.
 */

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String name;
    private Integer numPhotos = 0;
    private Integer capacity;
    private Calendar earliest;
    private Calendar latest;

    private Photo[] photos = null;

    /**
     * Construct an album that stores photos in it and choose options on what to do
     * @param uname userName
     * @param alname albumname
     */
    public Album (String uname, String alname) {
        userName = uname;
        name = alname;
        earliest = Calendar.getInstance();
        latest = Calendar.getInstance();

        numPhotos = 0;
        capacity = 200;
        photos = new Photo[capacity];
        for (int i = 0; i < capacity; i++) {
            photos[i] = null;
        }
    }


    /**
     * Get the name of the album
     * @return the name of the album
     */
    public String getName() {
        return name;
    }



    /**
     * Set the new name of the album
     * @param name the new name of the album
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Get the photos in the album
     * @return the photos in the album
     */
    public Photo[] getPhotos() {
        return photos;
    }


    /**
     * Get the photo by its filename
     * @param fname the filename for the photos
     * @return the photo its filename matches
     */
    public Photo getPhotoByFilename(String fname) {
        for (int i = 0; i < capacity; i++) {
            if(photos[i] == null ) {
                break;
            }
            else if(photos[i].getFileName().compareToIgnoreCase(fname) == 0){
                return photos[i];
            }
        }
        return null;
    }


    /**
     * Add a photo to the album, adjust the earliest or latest timestamp when necessary
     * @param photo the photo that needs to be added
     * @return true: if the photo is added; false: if there's duplicate
     */
    public boolean addPhoto(Photo photo) {
        if (numPhotos < capacity) {
            for (int i = 0; i < numPhotos; i++) {
                if (photos[i].getFileName().compareToIgnoreCase(photo.getFileName()) == 0) {
                    return false;
                }
            }
            photos[numPhotos] = photo;
            numPhotos++;
            latest = Calendar.getInstance();
        }

        for (int i = 0; i < numPhotos; i++) {
            if (earliest.getTimeInMillis() > photo.getCapturedDate().getTimeInMillis()) {
                earliest.setTimeInMillis(photo.getCapturedDate().getTimeInMillis());
            }
            else if (latest.getTimeInMillis() < photo.getCapturedDate().getTimeInMillis()) {
                latest.setTimeInMillis(photo.getCapturedDate().getTimeInMillis());
            }
        }
        return true;
    }


    /**
     * Delete a photo in the album with a filename, adjust the earliest or latest timestamp when necessary
     * @param photonFileName the photo's filename
     * @return true if the photo is deleted from the album, false otherwise
     */
    public boolean deletePhoto(String photonFileName) {
        for (int i = 0; i < numPhotos; i++) {
            if (photos[i].getFileName().compareToIgnoreCase(photonFileName) == 0) {
                for (int j = i; j < numPhotos; j++) {
                    photos[j] = photos[j+1];
                }
                numPhotos--;

                // adjust the earliest or latest timestamp when necessary
                Calendar temp =  earliest;
                earliest = latest;
                latest = temp;
                for (int j = 0; j < numPhotos; j++) {
                    if (earliest.getTimeInMillis() > photos[j].getCapturedDate().getTimeInMillis()) {
                        earliest.setTimeInMillis(photos[j].getCapturedDate().getTimeInMillis());
                    }
                    else if (latest.getTimeInMillis() < photos[j].getCapturedDate().getTimeInMillis()) {
                        latest.setTimeInMillis(photos[j].getCapturedDate().getTimeInMillis());
                    }
                }
                return true;
            }
        }
        return false;
    }


    /**
     * Get the number of photos inside of the album
     * @return the number of photos
     */
    public Integer getNumPhotos() {
        if (numPhotos == null) {
            numPhotos = 0;
        }
        else {
            for (int i = 0; i < capacity; i++) {
                if (photos[i] == null) {
                    numPhotos = i;
                    break;
                }
            }
        }
        return numPhotos;
    }


    /**
     * Get the earliest date of all photos of the album
     * @return the earliest date of all photos of the album
     */
    public Calendar getEarliest() {
        return earliest;
    }


    /**
     * Get the latest date of all photos of the album
     * @return the latest date of all photos of the album
     */
    public Calendar getLatest() {
        return latest;
    }


    /**
     * Return the string representation of the Album
     * @return the string representation of the Album
     */
    public String toString() {
        return "(" + userName + ", " + name + ", " + numPhotos + ", " + earliest + ", " + latest + ")";
    }

}
