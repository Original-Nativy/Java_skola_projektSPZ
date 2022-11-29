package com.example.new_project_java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
    getMenuInflater().inflate(R.menu.menu_item,menu);
    return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if((int) id == R.id.detect)
        {
        Intent intent = new Intent(MainActivity.this, Activity_Detect.class);
        startActivity(intent);
        return true;
        }
        else if((int) id == R.id.options)
        {
            Intent intent = new Intent(MainActivity.this,Activity_games.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

}