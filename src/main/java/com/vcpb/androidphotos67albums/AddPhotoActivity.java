package com.vcpb.androidphotos67albums;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.support.v4.content.FileProvider;
import android.content.ContentResolver;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;


/**
 * @version 1.0
 * @author Victor Chen, Paul Barbaro
 *
 * Add Photo Screen, require to:
 *  select a phone from the phones available in the assets/pictures
 *  select 20 pts When a photo is displayed, you should be able to add a tag to a photo. Only person and location are valid tag types; there are no typeless tags.
 *
 */
public class AddPhotoActivity extends AppCompatActivity implements OnItemSelectedListener, OnCheckedChangeListener {
    private TextView message;
    private Album album = null;
    private String albumname = null;
    private Spinner photoFileSpinner = null;
    private RadioGroup tagnameRadioGroup = null;
    private RadioButton personRadioSelection = null;
    private RadioButton locationRadioSelection = null;
    private Button addPhotoButton = null;

    String photofilename = null;
    String tagName = null;

    /**
     * Photo screen creation:
     * The open album activity of this assignment
     * @param savedInstanceState the previous saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        Bundle bundle = getIntent().getExtras();

        // Get all parameters and UI Ids
        if (bundle != null) {
            albumname = bundle.getString(AlbumActivity.ALBUM_NAME);
            album = PhotosAlbums.useralbums.getAlbumByname(albumname);
            ((TextView) findViewById(R.id.addPhotoTextview)).setText("Add Photo to Album: " + albumname);
        }

        message = (TextView)findViewById(R.id.addPhotoMessage);
        message.setText("Please select a Photo and its tag, add the tag value then click on Add Photo Button");
        photoFileSpinner = (Spinner)findViewById(R.id.addPhotoSpinner);
        photoFileSpinner.setOnItemSelectedListener(this);
        //photoFileSpinner.setBackgroundColor(Color.TRANSPARENT);
        tagnameRadioGroup = (RadioGroup)findViewById(R.id.addPhotoRadioGroup);
        tagnameRadioGroup.clearCheck();
        tagnameRadioGroup.setOnCheckedChangeListener(this);
        personRadioSelection = (RadioButton)findViewById(R.id.addPhotoRadioButtonPerson);
        locationRadioSelection = (RadioButton)findViewById(R.id.addPhotoRadioButtonLocation);
        addPhotoButton = (Button)findViewById(R.id.addPhotoButton);

        try {
            String[] list = PhotosAlbums.assetManager.list(PhotosAlbums.assetPhotoDirectory);
            System.out.println("list " + list.toString());

            List<String> arrAyList = new ArrayList<String>();
            for (int i = 0; i < list.length; i++) {
                System.out.println(" " + list[i]);
                arrAyList.add(list[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrAyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            photoFileSpinner.setAdapter(adapter);
        }
        catch (IOException e) {
            System.out.println("Exception " + e);
        }
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
        message.setText("Selected Photo file name: " + parent.getItemAtPosition(pos).toString());
        photofilename = parent.getItemAtPosition(pos).toString();
    }


    /**
     * No item was selected.
     *
     * @param parent    the Spinner in that no item is selected
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        photofilename = null;
        message.setText("Selected Photo file name: none");
    }


    /**
     * when the checked radio button has changed. When the selection is cleared, checkedId is -1
     * @param group     the Radio Group in that a radio button is selected/deselected
     * @param checkedId the ID of the radio button is selected
     */
    public void onCheckedChanged (RadioGroup group, int checkedId) {
        if (checkedId == R.id.addPhotoRadioButtonPerson) {
            message.setText("Selected Tag name: Person");
            tagName = "person";
        }
        else if (checkedId == R.id.addPhotoRadioButtonLocation) {
            message.setText("Selected Tag name: Location");
            tagName = "location";
        }
        else {
            message.setText("Selected Tag name: Unknown");
            tagName = null;
        }
    }


    /**
     * To add a phone to the working album,
     * @param v     the view from where the Add Photo Button is pressed
     */
    public void processAddPhoto(View v) {
        if (v.getId() != addPhotoButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }
        String tagValue = ((EditText)findViewById(R.id.addPhotoTagvalue)).getText().toString().trim();

        if (photofilename == null || photofilename.length() <= 0) {
            message.setText("No file name for the photo selected, try again");
            return;
        }
        else if(tagName == null || tagName.length() <= 0) {
            message.setText("No tag name for the photo selected, try again");
            return;
        }
        else if(tagValue == null || tagValue.length() <= 0) {
            message.setText("No tag value entered, try again");
            return;
        }

        try {
            String filename = PhotosAlbums.assetPhotoDirectory + "/" + photofilename;
            InputStream filestream = PhotosAlbums.assetManager.open(filename);
            if (filestream != null) {
                System.out.println("File inputstream opened for : " + filename);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MILLISECOND, 0);
                Photo photo = new Photo(filename, filename, cal, new Tag(tagName, tagValue));
                if (album.addPhoto(photo) == true) {
                    PhotosAlbums.writeUserAlbumsToFile();
                    AlbumActivity.myAlbumActivity.displayPhotoThumberNails();
                    message.setText("Add Photo successful");
                }
                else {
                    message.setText("The photo is already existing in the album, try again");
                }
            } else {
                message.setText("Could not find the photo file. try again");
            }
        }
        catch (java.io.IOException e) {
            System.out.println("IO Exception: " + e);
        }

    }
}
