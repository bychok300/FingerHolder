package holder.bychek.com.fingerholder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import holder.bychek.com.fingerholder.Utils.TimeUtils;
import holder.bychek.com.fingerholder.Utils.Utils;

public class LeadersFragment extends Fragment {

    private Map<String, Long> userTotTimeHold = new HashMap<>();
    ListView leadersListView;


    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaders, container, false);
        //инициализируемм массив с лидерами
        final ArrayList<String> leadersArray = new ArrayList<>();
        //найдём наш список
        leadersListView = (ListView) view.findViewById(R.id.list_of_leaders);
        //пойдём в базу и вытащим оттуда общее время удержания кнопки
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("users");
        //реализуем листнер евентов
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("email").getValue(String.class); // пока вместо имени юзаем имеил
                    String totalTimeHolding = ds.child("totalTimeHolding").getValue(String.class);
                    //сконвертируем для удобства подсчета лидеров
                    long totalTime = TimeUtils.timeToSeconds(totalTimeHolding);
                    //положим всё в мапу для отображения имени и его рекорда
                    userTotTimeHold.put(name, Long.valueOf(totalTime));

                }
                //отсортируем по количеству времени удержания кнопки
                userTotTimeHold = Utils.sortByComparator(userTotTimeHold, "asc"); //TODO поправить тип сортировки
                //теперь положим все значения в список(потому что будем класть всё в адаптер)
                List<String> list = new ArrayList<>();
                for (Map.Entry<String, Long> entry : userTotTimeHold.entrySet()) {
                    list.add(entry.getKey() + " : \n" + TimeUtils.secondsToTime(entry.getValue()));
                }
                //установим адаптер который выведет всё на экран и превратит массив в listView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_list_item_activated_1,
                        list);
                leadersListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Не удалось считать данные по причине : " + databaseError.getMessage(), Toast.LENGTH_LONG ).show();
            }
        };
        //установим слушатель
        usersdRef.addListenerForSingleValueEvent(eventListener);
        return view;
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
