package com.vcpb.androidphotos67albums;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author Victor Chen, Paul Barbaro
 *
 * The photo display screen should include an option for a slideshow, allowing you to go backward or forward in the album one photo at a time with manual controls.
 * 20 pts When a photo is displayed, you should be able to add a tag to a photo.
 *  Only person and location are valid tag types; there are no typeless tags.
 *  You should also be able to delete a tag from a photo. Note: When displaying a photo, tags (if any) should be visible.
 */
public class DisplayPhotoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String albumname;
    private String photoname;
    private Album album;
    private int photoIndex;
    private Photo[] photos;

    private TextView photonameText, message;
    private ImageView photoImageView;
    private RadioGroup tagnameRadioGroup = null;
    private RadioButton personRadioSelection = null;
    private RadioButton locationRadioSelection = null;
    private EditText tagValue;
    private ScrollView tagListView;
    private Spinner spinner;
    private Button addTagButton, deleteTagButton, clearTagButton, leftSlideshowButton, rightSlideshowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumname = bundle.getString(AlbumActivity.ALBUM_NAME);
            photoname = bundle.getString(AlbumActivity.PHOTO_NAME);
            album = PhotosAlbums.useralbums.getAlbumByname(albumname);
        }

        photonameText = (TextView)findViewById(R.id.displayPhotoTextView);
        photoImageView = (ImageView)findViewById(R.id.displayPhotoImageView);
        tagnameRadioGroup = (RadioGroup)findViewById(R.id.displayPhotoRadioGroup);
        tagnameRadioGroup.clearCheck();
        personRadioSelection = (RadioButton)findViewById(R.id.displayPhotoRadioButtonPerson);
        locationRadioSelection = (RadioButton)findViewById(R.id.displayPhotoRadioButtonLocation);
        tagValue = (EditText)findViewById(R.id.displayPhotoTagValueTextValue);
        tagListView = (ScrollView)findViewById(R.id.displayPhotoTaglist);
        spinner = (Spinner)findViewById(R.id.displayPhotoSpinner);
        spinner.setOnItemSelectedListener(this);

        addTagButton = (Button)findViewById(R.id.displayPhotoAddTagButton);
        deleteTagButton = (Button)findViewById(R.id.displayPhotoDeleteTagButton);
        clearTagButton = (Button)findViewById(R.id.displayPhotoClearTagButton);
        leftSlideshowButton = (Button)findViewById(R.id.displayPhotoLeftSlideshowButton);
        rightSlideshowButton = (Button)findViewById(R.id.displayPhotoRightSlideshowButton);

        message = (TextView)findViewById(R.id.displayPhotoMessage);

        photos = AlbumActivity.myAlbumActivity.album.getPhotos();

        // Figure out photo inex for the slideshow
        if (photoname == null) {
            photoIndex = 0;
            message.setText("No photo is selected for view, display 1st photo");
        }
        else {
            for (int i = 0; i < AlbumActivity.myAlbumActivity.album.getNumPhotos(); i++) {
                String filename = photos[i].getFileName();
                int idx = filename.indexOf('/');
                if (photoname.compareTo(filename.substring(idx + 1)) == 0) { // match
                    photoIndex = i;
                    break;
                }
            }

            if (photoIndex >= AlbumActivity.myAlbumActivity.album.getNumPhotos()) {
                photoIndex = 0;
            }
            message.setText("Photo for view: " + photoname);
        }
        displayPhoto();
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
        if (parent.getId() != spinner.getId()) {
            message.setText("Not Spiner, try again.");
            return;
        }
        String selectedTagname = (String)parent.getItemAtPosition(pos);
        if (selectedTagname != null) {
            int idx = selectedTagname.indexOf(", ");
            String tagName = selectedTagname.substring(1, idx);
            String tagvalue = selectedTagname.substring(idx+2, selectedTagname.length() - 1);
            tagValue.setText(tagvalue);
            if (tagName.compareToIgnoreCase("location") == 0) {
                locationRadioSelection.setEnabled(true);
                locationRadioSelection.setChecked(true);
                personRadioSelection.setEnabled(false);
                personRadioSelection.setChecked(false);
            }
            else if (tagName.compareToIgnoreCase("person") == 0) {
                locationRadioSelection.setEnabled(false);
                locationRadioSelection.setChecked(false);
                personRadioSelection.setEnabled(true);
                personRadioSelection.setChecked(true);
            }
            else {
                locationRadioSelection.setChecked(false);
                personRadioSelection.setChecked(false);
                tagnameRadioGroup.clearCheck();
            }
            message.setText("Selected tag: " + selectedTagname);
        }
        else {
            message.setText("No tag selected.");
        }


        //photofilename = parent.getItemAtPosition(pos).toString();
    }


    /**
     * No item was selected.
     *
     * @param parent    the Spinner in that no item is selected
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        // photofilename = null;
        message.setText("Selected Photo file name: none");
    }


    /**
     * To add a tag to the working photo,
     * @param v     the view from where the Add Tag Button is pressed
     */
    public void processAddTag(View v) {
        if (v.getId() != addTagButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }

        String tagname = null;

        if (personRadioSelection.isChecked()) {
            tagname = "person";
        }
        else if (locationRadioSelection.isChecked()) {
            tagname = "location";
        }

        String tagvalue = tagValue.getText().toString().trim();
        if (tagname == null || tagname.length() <= 0) {
            message.setText("No tag name checked, try again.");
        }
        else if (tagvalue == null || tagvalue.length() <= 0) {
            message.setText("No tag value entered, try again.");
        }
        else {
            Tag atag = photos[photoIndex].getTagByName(tagname, tagvalue);
            if (atag != null && tagname.compareToIgnoreCase(atag.getName()) == 0 && tagvalue.compareToIgnoreCase(atag.getValue()) == 0) {
                message.setText("The tag name/value pair existed already.");
            }
            else {
                Tag newtag = new Tag(tagname, tagvalue);
                photos[photoIndex].addTag(newtag);
                displayTagsSpinner(photos[photoIndex].getTags());
                PhotosAlbums.writeUserAlbumsToFile();
                message.setText("New tag is added.");
            }
        }

    }



    /**
     * To add a tag to the working photo,
     * @param v     the view from where the Add Tag Button is pressed
     */
    public void processDeleteTag(View v) {
        if (v.getId() != deleteTagButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }

        String tagname = null;

        if (personRadioSelection.isChecked()) {
            tagname = "person";
        }
        else if (locationRadioSelection.isChecked()) {
            tagname = "location";
        }

        String tagvalue = tagValue.getText().toString().trim();
        if (tagname == null || tagname.length() <= 0) {
            message.setText("No tag name checked, try again.");
        }
        else if (tagvalue == null || tagvalue.length() <= 0) {
            message.setText("No tag value entered, try again.");
        }
        else {
            Tag atag = photos[photoIndex].getTagByName(tagname, tagvalue);
            if (atag != null && tagname.compareToIgnoreCase(atag.getName()) == 0 && tagvalue.compareToIgnoreCase(atag.getValue()) == 0) {
                photos[photoIndex].deleteTag(tagname,tagvalue);
                displayTagsSpinner(photos[photoIndex].getTags());
                PhotosAlbums.writeUserAlbumsToFile();
                message.setText("the tag is deleted.");
            }
            else {
                message.setText("The tag name/value pair not found.");
           }
        }

    }

    /**
     * To clear the tag data
     * @param v     the view from where the Clear Button is pressed
     */
    public void processClearTag(View v) {
        if (v.getId() != clearTagButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }

        tagValue.setText("");
        tagnameRadioGroup.clearCheck();
        locationRadioSelection.setEnabled(true);
        personRadioSelection.setEnabled(true);
        message.setText("Tag name and value are cleared.");
    }


    /**
     * To display the phto on the left
     * @param v     the view from where the Go Left Button is pressed
     */
    public void processPreviousPhoto(View v) {
        if (v.getId() != leftSlideshowButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }

        if (photoIndex > 0) {
            photoIndex--;
            displayPhoto();
        }
        else {
            message.setText("No more photos to go left on the slideshow.");
        }
    }


    /**
     * To display the phto on the right
     * @param v     the view from where the Go Right Button is pressed
     */
    public void processNextPhoto(View v) {
        if (v.getId() != rightSlideshowButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }

        if (photoIndex < (album.getNumPhotos() - 1)) {
            photoIndex++;
            displayPhoto();
        }
        else {
            message.setText("No more photos to go right on the slideshow.");
        }
    }

    /**
     * Displayibg the photo
     */
    private void displayPhoto() {
        String filename  = photos[photoIndex].getFileName();

        try {
            InputStream is = PhotosAlbums.assetManager.open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            photoImageView.setImageBitmap(bitmap);
            int idx = filename.indexOf('/');
            photonameText.setText("Displaying photo: " + filename.substring(idx+1));

            Tag[] tags = photos[photoIndex].getTags();
            tagValue.setText(tags[0].getValue());
            if (tags[0].getName().compareToIgnoreCase("location") == 0) {
                locationRadioSelection.setEnabled(true);
                locationRadioSelection.setChecked(true);
                personRadioSelection.setEnabled(false);
                personRadioSelection.setChecked(false);
            }
            else if (tags[0].getName().compareToIgnoreCase("person") == 0) {
                locationRadioSelection.setEnabled(false);
                locationRadioSelection.setChecked(false);
                personRadioSelection.setEnabled(true);
                personRadioSelection.setChecked(true);
            }
            else {
                locationRadioSelection.setEnabled(false);
                locationRadioSelection.setChecked(false);
                personRadioSelection.setEnabled(false);
                personRadioSelection.setChecked(false);
            }

            displayTagsSpinner(tags);

        }
        catch (IOException ioe) {
            System.out.println("Error open Photo File for display: " + ioe);
            message.setText("Error display Photo file: " + photoname);
        }

    }


    /**
     * Display tags in the spinner
     * @param tags, the tags need to be shown
     */
    private void displayTagsSpinner(Tag[] tags) {
        List<String> arrAyList = new ArrayList<String>();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] == null) {
                break;
            }
            arrAyList.add(tags[i].toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrAyList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
