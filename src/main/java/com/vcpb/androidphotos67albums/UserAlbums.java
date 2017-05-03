package com.vcpb.androidphotos67albums;

import java.io.Serializable;

/**
 * Created by Victor Chen and Paul Barbaro on 4/23/2017.
 */

public class UserAlbums implements Serializable {
    private static final long serialVersionUID = 1L;
    private String loginName;

    private Album[] albums = null;
    private Integer numAlbums;
    private Integer capacity;

    /**
     * Constructor
     * @param n		loginName
     */
    public UserAlbums (String n) {
        loginName = n;

        capacity = 40;
        numAlbums = 0;

        albums = new Album[capacity];
        for (int i = 0; i < capacity; i++) {
            albums[i] = null;
        }
    }


    /**
     * Get the loginName
     * @return loginName
     */
    public String getLoginName() {
        return loginName;
    }


    /**
     * Get the albums of the user
     * @return the albums of the user
     */
    public Album[] getAlbums() {
        return albums;
    }


    /**
     * Get the album by its name
     * @param aname album name
     * @return the album its name matches
     */
    public Album getAlbumByname(String aname) {
        for (int i = 0; i < capacity; i++) {
            if(albums[i] == null ) {
                break;
            }
            else if(albums[i].getName().compareTo(aname) == 0) {
                return albums[i];
            }
        }
        return null;
    }


    /**
     * Add a album to the user
     * @param album the album to be added
     */
    public void addAlbum(Album album) {
        if (numAlbums < capacity) {
            albums[numAlbums] = album;
            numAlbums++;
        }
    }


    /**
     * Delete a album with a name
     * @param albumName the name of the album that to be deleted
     * @return true if the album is deleted from the user, false otherwise
     */
    public boolean deleteAlbum(String albumName) {
        for (int i = 0; i < numAlbums; i++) {
            if (albums[i].getName().compareTo(albumName) == 0) {
                for (int j = i; j < numAlbums; j++) {
                    albums[j] = albums[j+1];
                }
                numAlbums--;

                return true;
            }
        }
        return false;
    }


    /**
     * Get the number of albums of a user
     * @return the number of albums of a user
     */
    public int getNumAlbums() {
        return numAlbums;
    }


    /**
     * Overwrite toString
     */
    public String toString() {
        return "(" + loginName + ", " + numAlbums + ")";
    }

}
