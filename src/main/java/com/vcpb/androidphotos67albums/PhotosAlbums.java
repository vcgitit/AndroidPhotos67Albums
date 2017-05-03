package com.vcpb.androidphotos67albums;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Toast;
import android.content.ContextWrapper;
import android.content.res.AssetManager;


/**
 * @version 1.0
 * @author Victor Chen, Paul Barbaro
 *
 * Per Android requirements, all the photos files are raw files for the assignemnt are put in assets/pictures directory of the app, shown in the Project view
 * of the Android Studio.
 *
 * Since the app will run on a personal smart phone, there is only a single user, who is the owner of the phone. So there is no logging in, and no admin functionality.
 * Also, explicit captions are not required for photos (the filename will stand for the caption). Dates are not required either.
 * Your app MUST run correcty on the Nexus 4 (4.7 inch, 768x1280, xhdpi) device emulator that comes with Android Studio.
 * Your app should run on Android API Level 24 (target SDK Version 24) - this is the default setting in the project creation wizard in Android Studio 2.3.
 * We will test only for this level.
 *
 * 30 pts Home screen. When the app comes up, it should load album and photo data from the previous session, if any, and list all albums with names in plain text.
 *   Off this "home" screen, you should be able to do the following, in one or more navigational steps.
 * 40 pts Open, create, delete, and rename albums as listed in the GUI project description. When opening an album, display all its photos, with their thumbnail images.
 * 40 pts Once an album is open, you should be able to add, remove, or display a photo.
 *   The photo display screen should include an option for a slideshow, allowing you to go backward or forward in the album one photo at a time with manual controls.
 * 20 pts When a photo is displayed, you should be able to add a tag to a photo. Only person and location are valid tag types; there are no typeless tags.
 *   You should also be able to delete a tag from a photo. Note: When displaying a photo, tags (if any) should be visible.
 * 20 pts You should be able to move a photo from one album to another
 * 50 pts You should be able to search for photos by tag (person or location), and matches should allow completion.
 *   For instance, if a location "New" is typed, matches should include photos taken in New York, New Mexico, New Zealand, etc.
 *   Note: You are not required to implement functionality to add new tag types, or save the matching photos in an album.
 */
public class PhotosAlbums extends AppCompatActivity implements OnTabSelectedListener, RadioGroup.OnCheckedChangeListener {

    private String loginname = new String("stock");

    // variable shared by the package
    static PhotosAlbums myPhotosAlbums;
    static UserAlbums useralbums = null;
    static AssetManager assetManager = null;
    static String assetPhotoDirectory = "pictures";
    static String userfilename = "stock.ser";
    static HashSet<String> searchResults = new HashSet<String>();


    private EditText albumName, albumNumPhotos;
    private TextView message;
    private ListView listviewAlbumName;
    private Button searchButton;
    private RadioGroup tagnameRadioGroup = null;
    private RadioButton personRadioSelection = null;
    private RadioButton locationRadioSelection = null;

    private ArrayList<String> albumNames = new ArrayList<String>();

    private String selectedAlbumname = null;
    private Album selectedAlbum = null;
    private String tagName = null;

    private ContextWrapper context = null;



    /**
     * Home screen creation:
     * The main activity of this assignment
     * @param savedInstanceState the previous saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_albums);

        myPhotosAlbums = this;

        // get all the fields
        albumName = (EditText)findViewById(R.id.editTextName);
        albumNumPhotos = (EditText)findViewById(R.id.editTextNumPhotos);
        message = (TextView)findViewById(R.id.mainMessage);
        listviewAlbumName = (ListView)findViewById(R.id.listViewAlbumNames);
        searchButton = (Button)findViewById(R.id.searchButton);
        tagnameRadioGroup = (RadioGroup)findViewById(R.id.searchPhotoRadioGroup);
        tagnameRadioGroup.clearCheck();
        tagnameRadioGroup.setOnCheckedChangeListener(this);
        personRadioSelection = (RadioButton)findViewById(R.id.searchPhotoRadioButtonPerson);
        locationRadioSelection = (RadioButton)findViewById(R.id.searchPhotoRadioButtonLocation);

        assetManager = getAssets();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setTabMode(1);

        ActionBar ab = getSupportActionBar();

        handleIntent(getIntent());

        context = new ContextWrapper(this);

        FileInputStream filestream = null;

        try {
            File serfile = new File(getFilesDir(), userfilename);
            System.out.println("IN file:" + serfile.getPath());
            Uri uri = Uri.fromFile(serfile);
            System.out.println("URI file:" + uri.getPath() + ", " + uri.toString());
            System.out.println("URI file:" + uri.getPath() + ", " + uri.toString());
            if (serfile.exists())	{
                filestream = openFileInput(userfilename);
                ObjectInput input = new ObjectInputStream(filestream);
                useralbums  = (UserAlbums)input.readObject();
                //System.out.println(useralbums.toString());
                input.close();
                if (useralbums != null) {
                    Album[] albums = useralbums.getAlbums();
                    for (int i = 0; i < useralbums.getNumAlbums(); i++) {
                        albumNames.add(albums[i].getName());
                    }
                    if (selectedAlbumname == null) {
                        selectedAlbumname = albums[0].getName();
                        displaySelectedAlbum(selectedAlbumname);
                        selectedAlbum = albums[0];
                    }
                }
                else {
                    useralbums = new UserAlbums(loginname);
                }
            }
        }
        catch(ClassNotFoundException ex){
            System.err.println("Cannot perform input. Class not found." + ex.getMessage());
        }
        catch(IOException ex){
            System.err.println("Cannot perform input." + ex.getMessage());
        }
        finally {
            try {
                filestream.close();
            }
            catch (Exception e) {

            }
            if (useralbums == null) {
                useralbums = new UserAlbums(loginname);
            }
            showAlbumList();
            message.setText("number of albums: " + String.valueOf(useralbums.getNumAlbums()));
        }

    }


    /**
     * show the list of albums
     */
    private void showAlbumList() {
        // Assign adapter to ListView
        listviewAlbumName.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, albumNames));

        // ListView Item Click Listener
        listviewAlbumName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) listviewAlbumName.getItemAtPosition(position);
                // Show Alert 
                Toast.makeText(getApplicationContext(), "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG).show();
                selectedAlbumname = itemValue;
                listviewAlbumName.setSelection(itemPosition);
                if (selectedAlbumname != null) {
                    displaySelectedAlbum(selectedAlbumname);
                    selectedAlbum = useralbums.getAlbumByname(selectedAlbumname);
                }
            }
        });
    }



    /**
     * An TAB was reselected. The reselected TAB can be figured out by its Text
     *
     * The corresponding action (NEW, DROP, RENAME, OPEN, FIND, CLEAR) will be performed
     *
     * @param tab    the TAB reselected
     */
    public void onTabReselected(TabLayout.Tab tab) {
        System.out.println("onTabReselected() tab name:" + tab.getText());
        if (tab.getText().toString().compareToIgnoreCase("clear") == 0) {
            clearAllDisplay();
        }
        else {
            processListItem(tab);
        }
    }


    /**
     * An TAB was selected. The selected TAB can be figured out by its Text
     *
     * The corresponding action (NEW, DROP, RENAME, OPEN, FIND, CLEAR) will be performed
     *
     * @param tab    the TAB selected
     */
    public void onTabSelected(TabLayout.Tab tab) {
        System.out.println("onTabSelected() tab name:" + tab.getText());
        if (tab.getText().toString().compareToIgnoreCase("clear") == 0) {
            clearAllDisplay();
        }
        else {
            processListItem(tab);
        }
    }

    /**
     * An TAB was unselected.
     *
     * @param tab    the TAB unselected
     */
    public void onTabUnselected(TabLayout.Tab tab) {
        System.out.println("onTabUnselected() tab name:" + tab.getText());
    }



    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        showAlbumList();
    }


    /**
     * when the checked radio button has changed. When the selection is cleared, checkedId is -1
     * @param group     the Radio Group in that a radio button is selected/deselected
     * @param checkedId the ID of the radio button is selected
     */
    public void onCheckedChanged (RadioGroup group, int checkedId) {
        if (checkedId == R.id.searchPhotoRadioButtonPerson) {
            message.setText("Selected Tag name: Person");
            tagName = "person";
        }
        else if (checkedId == R.id.searchPhotoRadioButtonLocation) {
            message.setText("Selected Tag name: Location");
            tagName = "location";
        }
        else {
            message.setText("Selected Tag name: Unknown");
            tagName = null;
        }
    }


    /**
     * To invoke the search Activity if the photo search has result
     * @param v     the view from where the Search Button is pressed
     */
    public void processSearch(View v) {
        if (v.getId() != searchButton.getId()) {
            message.setText("Incorrect Button, try again.");
            return;
        }
        String tagvalue = ((EditText)findViewById(R.id.searchTagValue)).getText().toString().trim();

        if(tagName == null || tagName.length() <= 0) {
            message.setText("No tag name for the search, try again");
            return;
        }
        else if(tagvalue == null || tagvalue.length() <= 0) {
            message.setText("No tag value entered for the search, try again");
            return;
        }

        // Do search
        message.setText("Start searching for (" + tagName + "," + tagvalue + ")");
        System.out.println("Start searching for (" + tagName + "," + tagvalue + ")");
        searchResults.clear();
        Album[] albums = useralbums.getAlbums();
        for (int i = 0; i < useralbums.getNumAlbums(); i++) {
            Photo[] photos = albums[i].getPhotos();
            for (int j = 0; j < albums[i].getNumPhotos(); j++) {
                Tag[] tags = photos[j].getTags();
                int k = 0;
                while (tags[k] != null) {
                    String name = tags[k].getName();
                    String value = tags[k].getValue();
                    if (tagvalue.length() < value.length()) {
                        value = value.substring(0, tagvalue.length());
                    }
                    if (name.compareToIgnoreCase(tagName) == 0 && value.compareToIgnoreCase(tagvalue) == 0) {   // found a match
                        String fname = photos[j].getFileName();
                        if (searchResults.contains(fname) == false) {
                            searchResults.add(fname);
                        }
                    }
                    k++;
                }
            }
        }

        System.out.println("searching completed for (" + tagName + "," + tagvalue + ")");

        if (searchResults.isEmpty()) {
            message.setText("Searching (" + tagName + "," + tagvalue + ") found no matching photos, try again");
        }
        else {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            startActivityForResult(intent, 0);
        }
    }


    /**
     * Handling the tab selection (reselection) event
     * @param tab the tab selected
     */
    private void processListItem(TabLayout.Tab tab) {
        if (tab.getText().toString().compareToIgnoreCase("create") == 0) {
            String albumname = albumName.getText().toString().trim();
            if (albumname != null && albumname.length() > 0) {
                if (useralbums.getAlbumByname(albumname) != null) {
                    message.setText("Album already existed.");
                }
                else {
                    useralbums.addAlbum(new Album (loginname, albumname));
                    albumNames.add(albumname);
                    writeUserAlbumsToFile();
                    message.setText("Add Album successfully completed.");
                    showAlbumList();
                }
            }
            else {
                message.setText("No album name provided");
            }
            showAlbumList();
        }
        else if (tab.getText().toString().compareToIgnoreCase("rename") == 0) {
            String albumname = albumName.getText().toString().trim();
            if (albumname != null && albumname.length() > 0) {
                if (useralbums.getAlbumByname(albumname) != null) {
                    message.setText("New name for the album already existed.");
                }
                else if (selectedAlbumname == null) {
                    message.setText("No name of album is selected.");
                }
                else {
                    Album album = useralbums.getAlbumByname(selectedAlbumname);
                    album.setName(albumname);
                    albumNames.add(albumname);
                    albumNames.remove(selectedAlbumname);
                    writeUserAlbumsToFile();
                    message.setText("Rename Album successfully completed.");
                    showAlbumList();
                }
            }
            else {
                message.setText("No album name provided");
            }
        }
        else if (tab.getText().toString().compareToIgnoreCase("delete") == 0) {
            if (selectedAlbumname == null) {
                message.setText("No name of album is selected.");
            }
            else {
                if (useralbums.deleteAlbum(selectedAlbumname)) {
                    albumNames.remove(selectedAlbumname);
                    writeUserAlbumsToFile();
                    message.setText("Delete Album successfully completed.");
                    showAlbumList();
                    clearAllDisplay();
                }
                else {
                    message.setText("Unable to delete selected album.");
                }
            }
        }
        else if (tab.getText().toString().compareToIgnoreCase("open") == 0) {
            if (selectedAlbumname == null) {
                message.setText("No album selected.");
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString(AlbumActivity.ALBUM_NAME, selectedAlbumname);
                Intent intent = new Intent(this, AlbumActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        }
        else {
            message.setText("Unknown TAB selected.");
        }
    }


    private void displaySelectedAlbum(String selectedAlbumName) {
        selectedAlbum = useralbums.getAlbumByname(selectedAlbumName);
        if (selectedAlbum != null) {
            albumName.setText(selectedAlbum.getName());
            albumNumPhotos.setText(String.valueOf(selectedAlbum.getNumPhotos()));
        }
    }

    public void clearAllDisplay() {
        albumName.setText("");
        albumNumPhotos.setText("");
        selectedAlbumname = null;
        selectedAlbum = null;
        listviewAlbumName.setSelection(-1);
    }


    /**
     * write the UserAlbums To File
     * each album file name consists of user login name and album name: loginname + ".ser"
     */
   static void writeUserAlbumsToFile()  {
        FileOutputStream writer = null;
        try {
            writer = PhotosAlbums.myPhotosAlbums.openFileOutput(PhotosAlbums.userfilename, 0);
            System.out.println("Out file: " + writer.getFD().toString());

            //serialize the List
            OutputStream buffer = new BufferedOutputStream(writer);
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(PhotosAlbums.useralbums);
            output.close();
        }
        catch(IOException ex){
            System.err.println("Cannot perform output: " + ex.getMessage());
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {

            }
        }
    }
}
