package com.android.acadgild.savefileasynctask161;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    //Declare required variables
    private int mDelay = 500;
    private static final int PERMISSION_REQUEST_CODE = 200;
    EditText etData;
    TextView tvShowData;
    Button btnAddData;
    Button btnDeleteFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if write external storage permission is granted or not
        if (!checkPermission()) {
            //If not then request permission
            requestPermission();

        } else {
            //Else display this message.
            Toast.makeText(MainActivity.this, "Permission already granted.", Toast.LENGTH_LONG).show();

        }

        etData = (EditText) findViewById(R.id.etAddText);
        tvShowData = (TextView) findViewById(R.id.tvShowFromFile);
        btnAddData = (Button) findViewById(R.id.btnAddText);
        btnDeleteFile = (Button) findViewById(R.id.btnDelFile);

        //Set button onclicklistener event to write and read .txt file to external storage using asynctask
        btnAddData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new LoadIconTask().execute(etData.getText().toString());
            }
        });

        //setOnClickListener for delete file button to delete file from external directory
        btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"testasyncfile"+".txt");
                tvShowData.setText("");
                boolean deleted = file.delete();
                Toast.makeText(MainActivity.this,"File Deleted - "+deleted, Toast.LENGTH_LONG).show();
                if(deleted==true)
                {
                    Toast.makeText(MainActivity.this,"File Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //Async class LoadIconTask
    class LoadIconTask extends AsyncTask<String, String, String> {




        @Override
        protected void onPreExecute() {

        }

        //doInBackground method of Async class
        @Override
        protected String doInBackground(String... resId) {


            FileOutputStream fos;
            try {

                //Code to write string data in .txt file to external storage
                showToast(Environment.getExternalStorageDirectory()+"/"+"testasyncfile"+".txt");
                //Log.e("External Dir","Dir-"+Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"testasyncfile"+".txt");
                //Log.e("resId[0]","resId[0]-"+resId[0]);
                File myFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"testasyncfile"+".txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(resId[0]);
                myOutWriter.close();
                fOut.close();
                showToast("File Saved");
                //Log.e("External Dir","Dir-"+Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+"testasyncfile"+".txt");


            } catch (FileNotFoundException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}


            //Read file from external storage and return string as result
            StringBuffer stringBuffer = new StringBuffer();
            String aDataRow = "";
            String aBuffer = "";
            try {
                File myFile = new File("/storage/emulated/0/"+"testasyncfile"+".txt");
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));

                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n";
                }
                myReader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            showToast(aBuffer);
           // Toast.makeText(MainActivity.this,aBuffer, Toast.LENGTH_LONG).show();

            return aBuffer;
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        //onPostExecute method to show result on textview after execution of async doInBackground method
        @Override
        protected void onPostExecute(String result) {
            tvShowData.setText(result);
        }

        private void sleep() {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                // Log.e(TAG, e.toString());
            }
        }

        //Show toast method using runOnUiThread to show toast during Async class execution
        private void showToast(final String text)
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //checkPermission method
    private boolean checkPermission() {
        //Take result for each permision by calling ContextCompat.checkSelfPermission method
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        //Return result
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    //requestPermission method
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    //onRequestPermissionsResult method
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Check for permission request code
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                //Check if length of grantResults array greater than 0
                if (grantResults.length > 0) {
                    //Take result of all 3 requests
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // If all 3 requests are granted then display this message
                    if (writeAccepted && readAccepted)
                        Toast.makeText(MainActivity.this, "Permission Granted, Now you can access external storage.", Toast.LENGTH_LONG).show();
                    else {
                        //Else dispay denied permission message.
                        Toast.makeText(MainActivity.this, "Permission Denied, You cannot access external storage.", Toast.LENGTH_LONG).show();
                        // Check if version is greater than Marshmallow or not
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                //call showMessageOKCancel method if all permissions are not granted
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    //Again call request permission
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }
    //Method to show message in dialog
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}

