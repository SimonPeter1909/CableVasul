package trickandroid.cablevasul.ActivityLogin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.StringTokenizer;


import mehdi.sakout.fancybuttons.FancyButton;
import trickandroid.cablevasul.ActivityArea.AreaActivity;
import trickandroid.cablevasul.FirebasePackage.InitializeFirebaseAuth;
import trickandroid.cablevasul.R;
import trickandroid.cablevasul.Utils.CheckInternet;
import trickandroid.cablevasul.Utils.DateSetter;
import trickandroid.cablevasul.Utils.ShowSnackBar;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //firebase
    private InitializeFirebaseAuth auth = new InitializeFirebaseAuth();

    private CheckBox checkBox;
    private EditText emailET, passwordET;
    private TextView dateTV;
    private FancyButton loginBTN;
    private ProgressBar progressBar;
    private MaterialDialog progressDialog;

    //sqlite
    private SQLiteDatabase rememberMe;

    //utils
    private ShowSnackBar snackBar = new ShowSnackBar();
    private DateSetter dateSetter = new DateSetter();
    private CheckInternet checkInternet = new CheckInternet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkInternetConnection();
        initializeWidgets();
        auth.initializeFBAuth();
        createSQL();
        fillFields();
        setDate();
    }

    /**
     * checks for internet connection and if the connection fails displays the dialog to check for internet connection
     */
    public void checkInternetConnection() {
        if (!checkInternet.isNetworkAvailable(this)) {
            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            dialog.setTitleText("No Internet Connection!!!");
            dialog.setContentText("Check your Internet Connection");
            dialog.setConfirmText("Exit Application");
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    /**
     * Login Users with email and password
     *
     * @param view
     */
    public void loginClick(View view) {
        String email = emailET.getText().toString() + "@simon.com";
        String password = passwordET.getText().toString();
        if (email.isEmpty()) {
            snackBar.snackBar(view, "Enter E-Mail");
        } else if (password.isEmpty()) {
            snackBar.snackBar(view, "Enter Password");
        } else {
            if (checkDate()) {
                MaterialDialog.Builder progressBuilder = new MaterialDialog.Builder(this)
                        .title("Logging You in")
                        .content("Please Wait...")
                        .progress(true,0)
                        .progressIndeterminateStyle(true);
                progressDialog = progressBuilder.build();
                progressDialog.setCancelable(false);
                progressDialog.show();
                loginUser(email, password, view);
            } else {
                snackBar.snackBar(view, "Check your Internet Connection");
            }
        }
    }

    /**
     * initialize widgets
     */
    public void initializeWidgets() {
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        loginBTN = findViewById(R.id.btn_spotify);
        loginBTN.setVisibility(View.GONE);
        dateTV = findViewById(R.id.dateTextView);
        checkBox = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * creates or opens database and Table to store email and password
     */
    public void createSQL() {
        rememberMe = openOrCreateDatabase("RememberMe", MODE_PRIVATE, null);
        rememberMe.execSQL("CREATE TABLE IF NOT EXISTS RememberMeTable(email TEXT NOT NULL, password TEXT NOT NULL)");
    }

    /**
     * Stores the value of email and password in sql database if the remember me checkBox is checked
     */
    public void rememberMe() {
        if (checkBox.isChecked()) {
            String emailWithAt = emailET.getText().toString();
            String[] email = emailWithAt.split("@");
            String password = passwordET.getText().toString();

            ContentValues fields = new ContentValues();
            fields.put("email", email[0]);
            fields.put("password", password);

            rememberMe.insert("RememberMeTable", null, fields);
        } else {
            rememberMe.delete("RememberMeTable", null, null);
        }
    }

    /**
     * When the app starts if the database is loaded with email and password, fills the email and password editText
     * with the values stored in the database
     */
    public void fillFields() {
        Cursor cursor = rememberMe.rawQuery("SELECT * FROM RememberMeTable", null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    String password = cursor.getString(cursor.getColumnIndex("password"));

                    emailET.setText(email);
                    passwordET.setText(password);

                    checkBox.setChecked(true);
                }
            }
        }
    }

    /**
     * gets the value form the dateTextView which is parsed from the time.us and checks it with dateSetter method
     *
     * @return
     */
    public boolean checkDate() {
        String date = dateTV.getText().toString();
        Log.d(TAG, "checkDate: = " + date);
        return date.equals(dateSetter.finalDate());
    }

    /**
     * parse the value of date from time.us
     */
    public void setDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dateStatement;
                String finalDate = "";
                try {
                    Document document = Jsoup.connect("https://time.is/Unix_time_now").get();
                    Elements start = document.select("div[title=Click for calendar]");
                    dateStatement = start.text();
                    StringTokenizer stringTokenizer = new StringTokenizer(dateStatement, ",");
                    String day = stringTokenizer.nextToken();
                    String monthAndDate = stringTokenizer.nextToken();
                    String year = stringTokenizer.nextToken();
                    finalDate = day + ", " + monthAndDate + ", " + year;

                    Log.d(TAG, "run: running");
                } catch (IOException e) {
                    Log.d(TAG, "run: error " + e);
                    loginBTN.setText("Press to Reload");
                    loginBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (loginBTN.getText().toString().equals("Press to Reload")) recreate();
                        }
                    });
                }

                final String finalDate1 = finalDate;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: date = " + finalDate1);
                        loginBTN.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        dateTV.setText(finalDate1);
                    }
                });

            }
        }).start();

    }

    /**
     * firebase method to login User
     *
     * @param email
     * @param password
     * @param view
     */
    public void loginUser(String email, final String password, final View view) {
        auth.mAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: password = " + password);
                            snackBar.snackBar(view, "Login Successfull");
                            rememberMe();
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, AreaActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("Incorrect Username or Password")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthListner();
    }
}
