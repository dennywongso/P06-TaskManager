package com.myapplicationdev.android.p06_taskmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Task> tasks;
    ArrayAdapter<Task> adapter;
    Button btnAdd;
    int actReqCode = 1;
    int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        final DBHelper dbh = new DBHelper(this);
        CharSequence reply = null;
        Intent intent = getIntent();
        int id = intent.getIntExtra("key", 0);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            reply = remoteInput.getCharSequence("status");
        }
        if (reply != null) {
            if (reply.toString().equalsIgnoreCase("Completed")) {
                dbh.deleteTask(id);
            }
        }

        tasks = dbh.getAllTasks();
        adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
        lv.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                registerForContextMenu(view);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == actReqCode) {
            if (resultCode == RESULT_OK) {
                DBHelper dbh = new DBHelper(MainActivity.this);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getTitle().toString().equalsIgnoreCase("delete")) {
            DBHelper dbh = new DBHelper(this);
            Task task = tasks.get(selectedPosition);
            dbh.deleteTask(task.getId());
            tasks.clear();
            tasks.addAll(dbh.getAllTasks());
            adapter.notifyDataSetChanged();
            dbh.close();
        } else {

            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout updateTask = (LinearLayout) inflater.inflate(R.layout.updatetask, null);
            final EditText etName = (EditText) updateTask.findViewById(R.id.etName);
            final EditText etDesc = (EditText) updateTask.findViewById(R.id.etDesc);
            Task task = tasks.get(selectedPosition);
            etName.setText(task.getName().toString());
            etDesc.setText(task.getDescription().toString());
            final int idT1 = task.getId();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Task")
                    .setView(updateTask)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            DBHelper dbh = new DBHelper(MainActivity.this);
                            Toast.makeText(MainActivity.this,etName.getText().toString(),Toast.LENGTH_LONG).show();
                            Task t1 = new Task(idT1,etName.getText().toString(),etDesc.getText().toString());
                            dbh.updateTask(t1);
                            tasks.clear();
                            tasks.addAll(dbh.getAllTasks());
                            adapter.notifyDataSetChanged();
                            dbh.close();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }
}
