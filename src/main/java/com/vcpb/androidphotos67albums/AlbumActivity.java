package com.vcpb.androidphotos67albums;

import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.media.ThumbnailUtils;


/**
 * @version 1.0
 * @author Victor Chen, Paul Barbaro
 *
 * Album screen:
 * When opening an album, display all its photos, with their thumbnail images.
 * 40 pts Once an album is open, you should be able to add, remove, or display a photo.
 *  The photo display screen should include an option for a slideshow, allowing you to go backward or forward in the album one photo at a time with manual controls.
 * 20 pts When a photo is displayed, you should be able to add a tag to a photo. Only person and location are valid tag types; there are no typeless tags.
 */
public class AlbumActivity extends AppCompatActivity  implements TabLayout.OnTabSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String ALBUM_NAME = "albumName";
    public static final String PHOTO_NAME = "photoName";
    public static final String PHOTO_INDEX = "photoIndex";
    public static final String ALBUM = "album";

    static AlbumActivity myAlbumActivity;
    Album album = null;

    private TextView albumMessage;
    private TextView albumPhotonameText;
    private GridLayout photosThumbnailsGridLayout;
    private Spinner albumSpinner;
    private String albumname = null;
    private android.view.View prevView = null;
    private String selectedPhotoname = null;
    private String selectedAlnumname = null;

    /**
     * Album screen creation:
     * The open album activity of this assignment
     * @param savedInstanceState the previous saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Bundle bundle = getIntent().getExtras();

        // Get all parameters and UI Ids
        albumMessage = (TextView)findViewById(R.id.albumMessage);
        if (bundle != null) {
            albumname = bundle.getString(ALBUM_NAME);
            album = PhotosAlbums.useralbums.getAlbumByname(albumname);
            ((TextView) findViewById(R.id.textAlbumname)).setText("Album name: " + albumname);
            albumMessage = (TextView)findViewById(R.id.albumMessage);
            albumMessage.setText("Album name: " + albumname);
        }

        albumPhotonameText = (TextView)findViewById(R.id.albumPhotonameText);
        myAlbumActivity = this;
        TabLayout tabLayout = (TabLayout)findViewById(R.id.albumTabLayout);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setTabMode(1);
        album = PhotosAlbums.useralbums.getAlbumByname(albumname);
        photosThumbnailsGridLayout = (GridLayout)findViewById(R.id.albumGridLayout);
        photosThumbnailsGridLayout.setColumnCount(5);
        photosThumbnailsGridLayout.setRowCount(4);
        photosThumbnailsGridLayout.setPadding(10, 10, 10, 10);
        photosThumbnailsGridLayout.setBackgroundColor(Color.CYAN);
        displayPhotoThumberNails();

        albumSpinner = (Spinner)findViewById(R.id.albumSpinner);
        try {
            Album[] albums = PhotosAlbums.useralbums.getAlbums();
            List<String> arrAyList = new ArrayList<String>();
            for (int i = 0; i < PhotosAlbums.useralbums.getNumAlbums(); i++) {
                arrAyList.add(albums[i].getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrAyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            albumSpinner.setAdapter(adapter);
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
        }
        albumSpinner.setOnItemSelectedListener(this);
    }


    /**
     * An TAB was reselected. The reselected TAB can be figured out by its Text, for TabLayout.OnTabSelectedListener
     *
     * The corresponding action (ADD, DROP, OPEN, MOVE, CLOSE) will be performed
     *
     * @param tab    the TAB reselected
     */
    public void onTabReselected(TabLayout.Tab tab) {
        System.out.println("onTabReselected() tab name:" + tab.getText());
        if (tab.getText().toString().compareToIgnoreCase("add") == 0) {
            addPhoto();
        }
        else if (tab.getText().toString().compareToIgnoreCase("display") == 0) {
            displayPhoto();
        }
        else if (tab.getText().toString().compareToIgnoreCase("remove") == 0) {
            deletePhoto();
        }
        else if (tab.getText().toString().compareToIgnoreCase("move") == 0) {
            movePhoto();
        }
        else {
            albumMessage.setText("Unknown TAB selection: " + tab.getText().toString());
        }
    }


    /**
     * An TAB was selected. The selected TAB can be figured out by its Text, for TabLayout.OnTabSelectedListener
     *
     * The corresponding action (ADD, DROP, OPEN, MOVE, CLOSE) will be performed
     *
     * * @param tab    the TAB selected
     */
    public void onTabSelected(TabLayout.Tab tab) {
        System.out.println("onTabSelected() tab name:" + tab.getText());
        if (tab.getText().toString().compareToIgnoreCase("add") == 0) {
            addPhoto();
        }
        else if (tab.getText().toString().compareToIgnoreCase("display") == 0) {
            displayPhoto();
        }
        else if (tab.getText().toString().compareToIgnoreCase("remove") == 0) {
            deletePhoto();
        }
        else if (tab.getText().toString().compareToIgnoreCase("move") == 0) {
            movePhoto();
        }
        else {
            albumMessage.setText("Unknown TAB selection: " + tab.getText().toString());
        }
    }


    /**
     * An TAB was unselected. The unselected TAB can be figured out by its Text, for TabLayout.OnTabSelectedListener
     *
     * @param tab    the TAB unselected
     */
    public void onTabUnselected(TabLayout.Tab tab) {
        System.out.println("onTabUnselected() tab name:" + tab.getText());
    }


    /**
     * An item was selected. The selected item is parent.getItemAtPosition(pos)
     *
     * @param parent    the Spinner in that an item is selected
     * @param view      the view where the the Spinner is in
     * @param pos       the position of the item in the Spiner
     * @param id        the R id of the spinner
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        albumMessage.setText("Selected Album name: " + parent.getItemAtPosition(pos).toString());
        selectedAlnumname = parent.getItemAtPosition(pos).toString();
    }


    /**
     * No item was selected.
     *
     * @param parent    the Spinner in that no item is selected
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        selectedAlnumname = null;
        albumMessage.setText("Selected Album name: none");
    }


    /**
     * Call add Photo Activity
     */
    private void addPhoto() {
        Bundle bundle = new Bundle();
        bundle.putString(AlbumActivity.ALBUM_NAME, albumname);
        Intent intent = new Intent(this, AddPhotoActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }


    /**
     * Call display Photo Activity
     */
    private void displayPhoto() {
        if (selectedPhotoname == null) {
            albumMessage.setText("No photo is selected for display");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(AlbumActivity.ALBUM_NAME, albumname);
        bundle.putString(AlbumActivity.PHOTO_NAME, selectedPhotoname);
        Intent intent = new Intent(this, DisplayPhotoActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }


    /**
     * Move Photo
     */
    private void movePhoto() {
        if (selectedPhotoname == null) {
            albumMessage.setText("No photo is selected for move");
            return;
        }

        if (selectedAlnumname == null) {
            albumMessage.setText("No Album is selected for moving the photo");
            return;
        }

        if (selectedAlnumname.compareTo(albumname) == 0) {
            albumMessage.setText("The selected photo cannot move to the Album itself");
            return;
        }

        String fname = PhotosAlbums.assetPhotoDirectory + "/" + selectedPhotoname;
        Photo photo = album.getPhotoByFilename(fname);
        if (photo == null || album.deletePhoto(fname) == true) {
            myAlbumActivity.photosThumbnailsGridLayout.removeView(prevView);
            Album album = PhotosAlbums.useralbums.getAlbumByname(selectedAlnumname);
            album.addPhoto(photo);
            PhotosAlbums.writeUserAlbumsToFile();
            albumMessage.setText("Photo " + selectedPhotoname + " is moved to the album " + selectedAlnumname);
            albumPhotonameText.setText("No Photo is selected");
            myAlbumActivity.photosThumbnailsGridLayout.setSelected(false);
            selectedPhotoname = null;
        }
        else {
            albumMessage.setText("Unable to move a photo " + selectedPhotoname + " to the album " + selectedAlnumname);
        }
    }


    /**
     * Delete the selected photo
     */
    private void deletePhoto() {
        if (selectedPhotoname == null) {
            albumMessage.setText("No photo is selected for deletion");
            return;
        }

        if (album.deletePhoto(PhotosAlbums.assetPhotoDirectory + "/" + selectedPhotoname) == true) {
            myAlbumActivity.photosThumbnailsGridLayout.removeView(prevView);
            PhotosAlbums.writeUserAlbumsToFile();
            albumMessage.setText("Photo " + selectedPhotoname + " is deleted.");
            albumPhotonameText.setText("Please select a photo");
            myAlbumActivity.photosThumbnailsGridLayout.setSelected(false);
            selectedPhotoname = null;
        }
        else {
            albumMessage.setText("Unable to delete a photo " + selectedPhotoname);
        }
    }


    /**
     * Click on an Thumbernail to select it, for View.OnClickListener
     * @param view the view of the Thumbernail
     */
    public void onClick(View view) {
        System.out.println("View: " + view.toString() + ". Tag: " + view.getTag());
        view.setSelected(true);
        view.setBackgroundColor(Color.BLUE);
        selectedPhotoname = view.getTag().toString();
        albumPhotonameText.setText("Selected Photo: " + selectedPhotoname);
        albumMessage.setText("Selected a Photo successfully");
        if (prevView != null) {
            prevView.setBackgroundColor(Color.LTGRAY);
        }
        prevView = view;
    }


    /**
     * Display Photo ThumberNails
     */
    void displayPhotoThumberNails() {
        myAlbumActivity.photosThumbnailsGridLayout.removeAllViews();
        Photo[] photos = myAlbumActivity.album.getPhotos();
        for (int i = 0; i < myAlbumActivity.album.getNumPhotos(); i++) {
            try {
                System.out.println("Asset Photo File name: " + photos[i].getFileName());
                InputStream is = PhotosAlbums.assetManager.open(photos[i].getFileName());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Bitmap thumbbitmap = ThumbnailUtils.extractThumbnail(bitmap, 125, 125);
                ImageView imageView = new ImageView(myAlbumActivity);
                imageView.setImageBitmap(thumbbitmap);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setBackgroundColor(Color.LTGRAY);
                int idx = photos[i].getFileName().indexOf('/');
                imageView.setTag(photos[i].getFileName().substring(idx+1));
                myAlbumActivity.photosThumbnailsGridLayout.addView(imageView);
                imageView.setOnClickListener(myAlbumActivity);

            }
            catch (IOException ioe) {
                System.out.println("Error open Asset Photo File name: " + photos[i].getFileName());
            }
        }
        myAlbumActivity.albumPhotonameText.setText("Please select a photo");
        myAlbumActivity.photosThumbnailsGridLayout.setSelected(false);
        System.out.println("photosThumbnailsGridLayout: " + myAlbumActivity.photosThumbnailsGridLayout.getChildCount());
        myAlbumActivity.albumMessage.setText("Photos in Album " + myAlbumActivity.albumname + " : " + myAlbumActivity.photosThumbnailsGridLayout.getChildCount());
    }
}
