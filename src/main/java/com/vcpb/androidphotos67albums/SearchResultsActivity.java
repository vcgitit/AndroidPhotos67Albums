package com.vcpb.androidphotos67albums;

import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @version 1.0
 * @author Victor Chen, Paul Barbaro
 *
 *  50 pts You should be able to search for photos by tag (person or location), and matches should allow completion.
 *   For instance, if a location "New" is typed, matches should include photos taken in New York, New Mexico, New Zealand, etc.
 *   Note: You are not required to implement functionality to add new tag types, or save the matching photos in an album.
 */
public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView searChPhotoNameText;
    private GridLayout photosThumbnailsGridLayout;
    private ImageView imageview;
    private String selectedPhotoname = null;
    private android.view.View prevView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        searChPhotoNameText = (TextView)findViewById(R.id.searchPhotoNameMessage);
        photosThumbnailsGridLayout = (GridLayout)findViewById(R.id.searchGridLayout);
        photosThumbnailsGridLayout.setColumnCount(5);
        photosThumbnailsGridLayout.setRowCount(4);
        photosThumbnailsGridLayout.setPadding(10, 10, 10, 10);
        photosThumbnailsGridLayout.setBackgroundColor(Color.CYAN);
        imageview = (ImageView)findViewById(R.id.searchPhotoImageView);

        Iterator<String> it = PhotosAlbums.searchResults.iterator();
        String filename = null;
        String photoname = null;
        while (it.hasNext()) {
            try {
                filename = it.next();
                int idx = filename.indexOf("/");
                photoname = filename.substring(idx+1);
                System.out.println("Asset Photo File name: " + filename);
                InputStream is = PhotosAlbums.assetManager.open(filename);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Bitmap thumbbitmap = ThumbnailUtils.extractThumbnail(bitmap, 125, 125);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(thumbbitmap);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setBackgroundColor(Color.LTGRAY);
                imageView.setTag(photoname);
                photosThumbnailsGridLayout.addView(imageView);
                imageView.setOnClickListener(this);

            }
            catch (IOException ioe) {
                System.out.println("Error open Asset Photo File name: " + (PhotosAlbums.assetPhotoDirectory + "/" + photoname));
            }
        }

        View v = photosThumbnailsGridLayout.getChildAt(0);
        if (v != null) {
            photoname = v.getTag().toString();
            DisplayPhoto(photoname);
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
        DisplayPhoto(view.getTag().toString());
        if (prevView != null) {
            prevView.setBackgroundColor(Color.LTGRAY);
        }
        prevView = view;
    }


    /**
     * Displaying the photo with the provided photoname
     *
     * @param photoname the photoname to be displayed
     */
    private void DisplayPhoto(String photoname) {
        try {
            String filename = PhotosAlbums.assetPhotoDirectory + "/" + photoname;
            InputStream is = PhotosAlbums.assetManager.open(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageview.setImageBitmap(bitmap);
            searChPhotoNameText.setText("Displaying the photo " + photoname);
        }
        catch (IOException ioe) {
            searChPhotoNameText.setText("Unable to display the photo " + photoname + " an IO error " + ioe);
        }
    }
}
