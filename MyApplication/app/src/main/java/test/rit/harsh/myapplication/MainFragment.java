package test.rit.harsh.myapplication;

/**
 * Created by patil on 10/10/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class MainFragment extends Fragment {
    public static final String RECEIVE_JSON = "test.rit.harsh.myapplication.BGNotiService.RECEIVE_JSON";
    TextView rules_TV;
    TextView temp, ambient, unit;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ArrayList<History> historyArrayList;
    /*
    *   Listens to broadcast with the intent RECEIVE_JSON
     */
    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECEIVE_JSON)) {
                //String serviceJsonString = intent.getExtra("json");
                Log.d("Debug main broadcast", "Recevied");
                temp = (TextView) getActivity().findViewById(R.id.temp_text);
                Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/digital-7.ttf");

                temp.setTypeface(typeFace);


                ambient = (TextView) getActivity().findViewById(R.id.ambient_text);
                ambient.setTypeface(typeFace);

                unit = (TextView) getActivity().findViewById(R.id.unit);
                unit.setTypeface(typeFace);

                String value = intent.getStringExtra("value");
                String[] text = value.split(",");
                String temptext = text[0];
                String ambienttext = text[1];
                temp.setText(temptext.replace("\"", ""));
                ambient.setText(ambienttext.replace("\"", ""));
                Calendar c = Calendar.getInstance();
                History history = new History(c.getTime().toString(), temptext.replace("\"", "") + "F");
                historyArrayList.add(history);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View newview = inflater.inflate(R.layout.fragment_main, container, false);

        //broadcast register
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_JSON);
        bManager.registerReceiver(bReceiver, intentFilter);


        if (savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            historyArrayList = new ArrayList<>();
        } else {
            historyArrayList = savedInstanceState.getParcelableArrayList("key");
        }
        recyclerView = (RecyclerView) newview.findViewById(R.id.history_view);
        recyclerView.setHasFixedSize(true);

        // Redundant recycler object
       /* History history = new History(Calendar.getInstance().getTime().toString(),"70"+"F");
        historyArrayList.add(history);
        History history2 = new History(Calendar.getInstance().getTime().toString(),"70"+"F");
        historyArrayList.add(history2);*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HistoryAdapter(getActivity(), historyArrayList);
        recyclerView.setAdapter(adapter);

        return newview;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key", historyArrayList);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}