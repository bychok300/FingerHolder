package holder.bychek.com.fingerholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateUI()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {userMoneyField.setText("Блаксов : " + counter);
            }
        }) ;
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
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                counter = dataSnapshot.getValue(Long.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(),"Не удалось считать значение из базы данных, обратитесь к администартору", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(),"Не удалось считать значение из базы данных, обратитесь к администартору", Toast.LENGTH_LONG).show();
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
                long elapsedMillis = SystemClock.elapsedRealtime() -c.getBase();
                if(elapsedMillis > 3600000L){
                    c.setFormat("0%s");
                }else{
                    c.setFormat("00:%s");
                }
            }
        });

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

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
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
