package holder.bychek.com.fingerholder;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import holder.bychek.com.fingerholder.Utils.Util;

public class GetMoneyActivity extends FragmentActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static long counter;
    private TextView userMoneyField;
    private Button clickForMoneyBnt, exitBtn, foundMoneyBtn;
    private Chronometer chronometer;
    private FirebaseUser currUser = auth.getCurrentUser();
    private String userId = currUser.getUid();
    private DatabaseReference usersdb = FirebaseDatabase.getInstance().getReference("users").child(userId);
    private String totalTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // вычитываем из базы значение заработанных монет
        usersdb.child("moneyAmount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                counter = dataSnapshot.getValue(Long.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(),"Не удалось считать значение из базы данных, обратитесь к администартору", Toast.LENGTH_LONG).show();
            }
        });

        // вычитываем из базы общее время когда была зажата кнопка
        usersdb.child("totalTimeHolding").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String totalTime = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Не удалось считать значение из базы данных, обратитесь к администартору", Toast.LENGTH_LONG).show();
            }
        });
        //главная кнопка
        clickForMoneyBnt = (Button) findViewById(R.id.clickForMoneyBnt);
        //поле где выводится количество заработанных монет
        userMoneyField = (TextView) findViewById(R.id.userAmountMoney);
        //вставим текст который увидет пользователь
        userMoneyField.setText(String.format("Блаксов : %d", counter));
        //инициализируем секундомер, для подсчета количества времени когда была зажата кнопка
        chronometer = (Chronometer) findViewById(R.id.chronometer);

        //устанавливаем листнер, который будет запускать наращивание монет если кнопка зажата
        clickForMoneyBnt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(clickForMoneyBnt.isPressed()) {
                            //увеличим значение монет
                            counter += 1;
                            System.out.println(counter);
                            updateUI();
                            //запишем это значение в базу
                            usersdb.child("moneyAmount").setValue(Long.valueOf(counter));
                        } else {
                            timer.cancel();

                        }
                    }
                },1000,2000); // повторяем каждые 2 секунды с задержкой в секунду

                return true;
            }
        });
        //листнер, который запускает хронометр как только кнопка нажимается и выключает как только она отжимается
        clickForMoneyBnt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //стартуем хронометр
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //останавливаем хронометр
                    chronometer.stop();
                    //запишем в базу время сколько была зажата кнопка
                    usersdb.child("localTimeHolding").setValue(chronometer.getText());
                    //запишем в базу сумму времени последнего зажатия с общим временем
                    String sumTime = Util.sumOfTwoTime(chronometer.getText().toString(), totalTime);
                    usersdb.child("totalTimeHolding").setValue(sumTime);
                }

                return false;
            }
        });

        //exit to login screen
        exitBtn = (Button) findViewById(R.id.exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GetMoneyActivity.this, LoginActivity.class));
                finish();
            }
        });
        //found money
        foundMoneyBtn = (Button) findViewById(R.id.found_money);
        foundMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 10.0){
                    Toast.makeText(getApplicationContext(), "Вывод средств возможен только, когда сумма выше 10", Toast.LENGTH_LONG).show();
                }else {
                    String userId = auth.getCurrentUser().getUid();
                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("founds").child(userId);
                    Map userInfo = new HashMap<>();
                    userInfo.put("id", userId);
                    userInfo.put("moneyAmount", String.format("%.6f", counter));
                    currentUserDb.updateChildren(userInfo);

                    Toast.makeText(getApplicationContext(), "Деньги поступят вам на счет в течении 3х банковских дней!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void updateUI()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {userMoneyField.setText("Блаксов : " + counter);
            }
        }) ;
    }

}
