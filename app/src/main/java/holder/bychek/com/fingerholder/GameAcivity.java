package holder.bychek.com.fingerholder;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import holder.bychek.com.fingerholder.Utils.Util;

public class GameAcivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        bottomNavigationView.setSelectedItemId(R.id.action_play_game);
        ft.replace(R.id.main_frame, new GameFragment()).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.action_play_game:
                                fragmentTransaction.replace(R.id.main_frame, new GameFragment()).commit();
                                break;
                            case R.id.action_statistic:
                                fragmentTransaction.replace(R.id.main_frame, new StatisticFragment()).commit();
                                break;
                            case R.id.action_menu:
                                fragmentTransaction.replace(R.id.main_frame, new MenuFragment()).commit();
                                break;
                            case R.id.action_multiplayer:
                                AlertDialog alertDialog = new AlertDialog.Builder(GameAcivity.this).create();
                                alertDialog.setTitle("Привет");
                                alertDialog.setMessage("Функция в разработке");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                bottomNavigationView.setSelectedItemId(R.id.action_play_game);
                                            }
                                        });
                                alertDialog.show();
                                break;
                        }
                        return true;
                    }
                });

    }

}
