package com.example.myapplication;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import static com.example.myapplication.PictureContent.downloadRandomImage;
import static com.example.myapplication.PictureContent.loadSavedImages;

public class ScrollingActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener{
    private RecyclerView recyclerView;
    private ScrollingActivity context;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadRandomImage(downloadManager, context);
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

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String filePath="";
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                Cursor c = downloadManager.query(q);

                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    }
                }
                c.close();
                PictureContent.loadImage(new File(filePath));
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
