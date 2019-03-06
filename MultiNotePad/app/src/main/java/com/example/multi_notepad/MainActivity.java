package com.example.multi_notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener
{

    private static final int B_REQUEST_CODE = 1;

    private static final String TAG = "MainActivity";
     List<Note> noteList = new ArrayList<>();
     RecyclerView recyclerView;
     NoteAdapter noteAdapter;
     Note removeNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
        setContentView(R.layout.activity_main);

        //Load json file objects into note list.
        //loadData();
        Log.d(TAG, "onCreate: Start async task");
        new MyAsyncTask(this).execute();
        Log.d(TAG, "onCreate: end async task");

//        getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#191970")));
//
//        recyclerView = findViewById(R.id.recyclerView);
//        noteAdapter = new NoteAdapter(noteList, this);
//        recyclerView.setAdapter(noteAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //This method will read the json file and store data into noteList
    private void loadData()
    {
        Log.d(TAG, "loadData: Reading JSON file");

        Note note = new Note();
        try
        {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));

            //new code for reading json array
            BufferedReader buff = new BufferedReader(new InputStreamReader(is, getString(R.string.encoding)));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = buff.readLine()) != null ){
                Log.d(TAG, "loadData: line:"+ line);
                sb.append(line);
            }

            String jsonStr = sb.toString();
            Log.d(TAG, "loadData: jsonStr: " + jsonStr);
            if(!(sb.toString().isEmpty()))
            {
                //JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray jsonarray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonarray.length(); i++)
                {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String title = jsonobject.getString("title");
                    String description = jsonobject.getString("description");
                    String date = jsonobject.getString("date");

                    noteList.add(new Note(title, description, new Date(date)));
                }
            }

            //InputStream is = getResources().openRawResource(R.raw.MyNotes);
//            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));
//            reader.beginObject();
//
//            while (reader.hasNext())
//            {
//                String name = reader.nextName();
//                switch (name) {
//                    case "title":
//                        note.setTitle(reader.nextString());
//                        break;
//                    case "description":
//                        note.setDescription(reader.nextString());
//                        break;
//                    case "date":
//                        note.setDate(new Date(reader.nextString()));
//                        break;
//                    default:
//                        reader.skipValue();
//                        break;
//                }
//            }
//
//            //populate Note List.
//            noteList.add(note);
//            reader.endObject();
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_LONG).show();
        }
        catch(Exception ex)
        {
            Log.d(TAG, "loadData: error reading file: ");
            ex.printStackTrace();
        }

    }

    //This is used for saving the data in JSON file while exiting the app.
    @Override
    protected void onPause()
    {
        saveNote();
        super.onPause();
    }

    //This method is for storing the products in JSON file.
    protected void saveNote()
    {
        Log.d(TAG, "saveNote: Saving JSON File");
        try {

            //new method for writing json array
//            JSONObject obj;
//            JSONArray jsonArray = new JSONArray();
//            for(int i=0; i<noteList.size(); i++)
//            {
//                obj = new JSONObject();
//                obj.put("title", noteList.get(i).getTitle());
//                obj.put("description", noteList.get(i).getDescription());
//                obj.put("date", noteList.get(i).getDate().toString());
//
//                jsonArray.put(obj);
//
//            }
//            Log.d(TAG, "saveNote: jsonarray: " + jsonArray.toString());
//
//            File file = new File(getString(R.string.file_name));
//            Writer output = new BufferedWriter(new FileWriter(file));
//            output.write(jsonArray.toString());
//            output.close();
//            Toast.makeText(getApplicationContext(), "Notes saved to file", Toast.LENGTH_LONG).show();


            //end of json array writing

            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            //FileOutputStream fos = getResources().openRawResource(R.raw.MyNotes);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginArray();

            for(int i=0; i<noteList.size(); i++)
            {
                writer.beginObject();
                writer.name("title").value(noteList.get(i).getTitle());
                writer.name("description").value(noteList.get(i).getDescription());
                writer.name("date").value(noteList.get(i).getDate().toString());
                writer.endObject();
                Log.d(TAG, "saveNote: writer: "+ i);
            }
            writer.endArray();
            writer.close();

            Log.d(TAG, "saveNote: list: "+ noteList);

            //Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    //This method ensures that menu is visible on the main activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }

    //This method performs action when any of the menu item is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.addNote:
                Note note = new Note();
               // Toast.makeText(this, "Add New Note", Toast.LENGTH_SHORT).show();
                addNote(note);
                getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
                return true;
            case R.id.aboutUs:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
                //Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
                return true;
            default:
                getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
                return super.onOptionsItemSelected(item);
        }
    }


    //This method will call Edit Activity when a user clicks on Add New Note.
    protected void addNote(Note note)
    {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("noteObject", note);
        startActivityForResult(intent, B_REQUEST_CODE);
    }

    //This method is called when data is returned from edit activity.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == B_REQUEST_CODE)
        {
            if (resultCode == -1)
            {
                Note updatedNote = (Note) data.getSerializableExtra("updatedNote");
                noteList.add(0, updatedNote);
                noteAdapter.notifyDataSetChanged();
                //String text = data.getStringExtra("updatedNote");
                //userText.setText(text);
                Toast.makeText(this, "Changes saved ! ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: Added new Note: " + resultCode);
            }
            //This is when user press cancel.
            else if(resultCode == 0){
                Toast.makeText(this, "Changes discarded ! ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: Discarded result Code: " + resultCode);
            }
            //This condition handles update request for the notes.
            else
            {
                Note updatedNote = (Note) data.getSerializableExtra("updatedNote");
                noteList.add(0, updatedNote);
                noteList.remove(removeNote);
                noteAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Changes Updated ! ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: Changes Updated result Code: " + resultCode);
            }
        }
        else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
        getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
    }

    // From OnClickListener
    @Override
    public void onClick(View v)
    {  // click listener called by ViewHolder clicks

        int pos = recyclerView.getChildLayoutPosition(v);
        Note note = noteList.get(pos);
        removeNote = note;
        //We delete this entry from the list to ensure that there is no duplicate entry in it.
        //noteList.remove(note);
        addNote(note);
        //Toast.makeText(v.getContext(), "SHORT " + note, Toast.LENGTH_SHORT).show();
    }

    // From OnLongClickListener
    @Override
    public boolean onLongClick(View v)
    {  // long click listener called by ViewHolder long clicks
        //Log.d(TAG, "onLongClick: list size: "+ noteList.size());
        checkDialogBox(v);
        getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
        return false;
    }

    void checkDialogBox(final View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.icon1);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                int pos = recyclerView.getChildLayoutPosition(v);
                Note note = noteList.get(pos);
                noteList.remove(note);
                //Log.d(TAG, "onLongClick: list size: "+ noteList.size());
                noteAdapter.notifyDataSetChanged();
                getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // Do Nothing.
            }
        });

        builder.setMessage("Are you sure you want to delete this item?");
        builder.setTitle("DELETE?");

        AlertDialog dialog = builder.create();
        dialog.show();
        getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
    }

    protected void populateList(ArrayList<Note> list)
    {
        this.noteList = list;

        if(list.get(0).getTitle().equalsIgnoreCase("file not found"))
        {
            Toast.makeText(this, this.getString(R.string.no_file), Toast.LENGTH_LONG).show();
            noteList.remove(0);
        }

        //Toast.makeText(this, this.getString(R.string.no_file), Toast.LENGTH_LONG).show();
        getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#191970")));

        recyclerView = findViewById(R.id.recyclerView);
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
