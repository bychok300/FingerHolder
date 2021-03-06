package holder.bychek.com.fingerholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import holder.bychek.com.fingerholder.Utils.TimeUtils;



public class GameFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static long counter;
    private TextView userMoneyField, totalTimeText;
    private Button clickForMoneyBnt;
    private Chronometer chronometer;
    private FirebaseUser currUser = auth.getCurrentUser();
    private String userId = currUser.getUid();
    private DatabaseReference usersdb = FirebaseDatabase.getInstance().getReference("users").child(userId);
    private String totalTime;
    private ImageView coinReward;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //обновление поля с количеством монет
    private void updateBalance() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                userMoneyField.setText(String.valueOf(counter));
            }
        });
    }
    //выставим видимость новой добавленной монетки
    private void setCoinVisible() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                coinReward.setVisibility(View.VISIBLE);
            }
        });

    }

    // сделаем фэйдаут (мягкое растворение) новой монетки
    private void fadeOutAndHideImage(final ImageView img) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setDuration(1000);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {

                    }

                    public void onAnimationEnd(Animation animation) {
                        img.setVisibility(View.GONE);
                    }

                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                img.startAnimation(fadeOut);
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_game, container, false);
        // вычитываем из базы значение заработанных монет
        usersdb.child("moneyAmount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                counter = dataSnapshot.getValue(Long.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "Не удалось считать значение из базы данных, обратитесь к администартору", Toast.LENGTH_LONG).show();
            }
        });

        // вычитываем из базы общее время когда была зажата кнопка
        usersdb.child("totalTimeHolding").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalTime = dataSnapshot.getValue(String.class);
                //на текствью выводим время общего удержания кнопки за всё время
                totalTimeText = (TextView) view.findViewById(R.id.totalTimeValue);
                totalTimeText.setText(totalTime);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Не удалось считать значение из базы данных, обратитесь к администартору." + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //главная кнопка
        clickForMoneyBnt = (Button) view.findViewById(R.id.clickForMoneyBnt);
        //поле где выводится количество заработанных монет
        userMoneyField = (TextView) view.findViewById(R.id.userAmountMoney);
        //вставим текст который увидет пользователь
        userMoneyField.setText(String.format("Блаксов : %d", counter));
        //инициализируем секундомер, для подсчета количества времени когда была зажата кнопка
        //меняем формат секундомера на 00:00:00
        chronometer = (Chronometer) view.findViewById(R.id.chronometer);
        chronometer.setFormat("00:%s");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer c) {
                long elapsedMillis = SystemClock.elapsedRealtime() - c.getBase();
                if (elapsedMillis > 3600000L) {
                    c.setFormat("0%s");
                } else {
                    c.setFormat("00:%s");
                }
            }
        });

        //инициализируем анимацию которая будет добавляться каждый раз когда увеличивается баланс
        coinReward = (ImageView) view.findViewById(R.id.coin_reward);

        //устанавливаем листнер, который будет запускать наращивание монет если кнопка зажата
        clickForMoneyBnt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (clickForMoneyBnt.isPressed()) {
                            //увеличим значение монет
                            counter += 1;
                            setCoinVisible();
                            fadeOutAndHideImage(coinReward);
                            System.out.println(counter);
                            updateBalance();
                            //запишем это значение в базу
                            usersdb.child("moneyAmount").setValue(Long.valueOf(counter));
                        } else {
                            timer.cancel();
                        }
                    }
                }, 0, 2000); // повторяем каждые 2 секунды
                return true;
            }
        });
        //листнер, который запускает хронометр как только кнопка нажимается и выключает как только она отжимается
        clickForMoneyBnt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //стартуем хронометр
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //останавливаем хронометр
                    chronometer.stop();
                    //запишем в базу время сколько была зажата кнопка
                    usersdb.child("localTimeHolding").setValue(chronometer.getText());
                    //запишем в базу сумму времени последнего зажатия с общим временем
                    String sumTime = TimeUtils.sumOfTwoTime(chronometer.getText().toString(), totalTime);
                    usersdb.child("totalTimeHolding").setValue(sumTime);
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //что произайдет когда фрагмент будет прикреплён
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
