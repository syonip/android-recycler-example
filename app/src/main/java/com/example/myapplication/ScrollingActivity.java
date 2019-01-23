package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import static com.example.myapplication.PictureContent.downloadRandomImage;
import static com.example.myapplication.PictureContent.loadSavedImages;

public class ScrollingActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener{
    private static final int REQUEST_CODE_READ = 2;
    private static final int REQUEST_CODE_WRITE = 3;
    private RecyclerView recyclerView;
    private ScrollingActivity context;
    private boolean writePermissionsGranted = false;
    private boolean readPermissionsGranted = false;

    public  boolean checkReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                readPermissionsGranted = true;
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ);
                return false;
            }
        }
        else {
            return true;
        }
    }

    public  boolean checkWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                writePermissionsGranted = true;
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkReadStoragePermissionGranted();
        checkWriteStoragePermissionGranted();

        context = this;
        final DownloadManager downloadmanager = (DownloadManager) getSystemService(context.DOWNLOAD_SERVICE);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadRandomImage(downloadmanager, context);
                    }
                });
            }
        });

        if (recyclerView == null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            recyclerView = (RecyclerView) currentFragment.getView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    readPermissionsGranted = true;
                } else {
                }
                break;

            case REQUEST_CODE_WRITE:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    writePermissionsGranted = true;

                } else {

                }
                break;
        }
        if (readPermissionsGranted && writePermissionsGranted) {
            init();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (readPermissionsGranted && writePermissionsGranted) {
            init();
        }
    }

    private void init() {
        BroadcastReceiver onComplete=new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadSavedImages(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onListFragmentInteraction(PictureItem item) {

    }
}
