package com.example.multi_notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class EditActivity extends AppCompatActivity
{

    private EditText titleEditText;
    private EditText descriptionEditText;
    Note updatedNote = new Note();
    private int result_code; //for updates

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#191970")));
        setContentView(R.layout.activity_edit);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        Intent intent = getIntent();
        if (intent.hasExtra("noteObject"))
        {
            Note note = (Note) intent.getSerializableExtra("noteObject");

           //This case will handle adding new notes
            if(note.equals(null))
            {
                //do nothing
            }
            //This case will handle editing notes.
            else
            {
                result_code = 1; //setting this value for handling update cases.
                titleEditText.setText(note.getTitle());
                descriptionEditText.setText(note.getDescription());
            }

            //textView.setText("ActivityB\nOpened from " + text);
        }
    }

    //This method ensures that menu is visible on the edit activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.save_note_menu, menu);
        return true;
    }

    //This method performs action when any of the menu item is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.saveNote:
                Toast.makeText(this, "Note Save Clicked", Toast.LENGTH_SHORT).show();
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void saveNote()
    {
        updatedNote.setTitle(titleEditText.getText().toString());
        updatedNote.setDescription(descriptionEditText.getText().toString());
        updatedNote.setDate(new Date());

        if(updatedNote.getTitle().trim().equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_LONG).show();
        }
        else if(updatedNote.getDescription().trim().equalsIgnoreCase(""))
        {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_LONG).show();
        }
        //This will save note and return to main activity
        else
        {
            //update case
            if(result_code == 1)
            {
                Intent data = new Intent(); // Used to hold data to be returned to original activity
                data.putExtra("updatedNote", updatedNote);
                setResult(1, data);
                finish(); // This closes the current activity, returning us to the original activity
            }
            //For adding new note case
            else
            {
                Intent data = new Intent(); // Used to hold data to be returned to original activity
                data.putExtra("updatedNote", updatedNote);
                setResult(-1, data);
                finish(); // This closes the current activity, returning us to the original activity
            }

        }
    }

    //Insert alert dialog to ask user to save or discard data.
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.icon1);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                saveNote();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // Pressing the back arrow closes the current activity, returning us to the original activity
                Intent data = new Intent(); // Used to hold data to be returned to original activity
                //For discard case
                setResult(0, data);
                finish(); // This closes the current activity, returning us to the original activity
            }
        });

        builder.setMessage("Do you want to save changes?");
        builder.setTitle("SAVE?");

        AlertDialog dialog = builder.create();
        dialog.show();

        //super.onBackPressed();
    }
}
