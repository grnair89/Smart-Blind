package test.rit.harsh.myapplication;

/**
 * Created by patil on 10/10/2015.
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class RuleFragment extends Fragment{

    public RuleFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    public ArrayList<RuleGetter> ruleArrayList;
    private FloatingActionButton fab;
    private boolean gender;
    Spinner spnTemp;
    Spinner spnOP;
    Spinner spnAmbient;
    Spinner spnBLind;
    public String datatoSend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key", ruleArrayList);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rule, container, false);

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            datatoSend = "getRules";
            new SendJSONRequest().execute();
            ruleArrayList = new ArrayList<>();
        }else{
            ruleArrayList = savedInstanceState.getParcelableArrayList("key");
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyle_view);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter( getActivity() , ruleArrayList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(onAddingListener());

        return rootView;
    }
    private View.OnClickListener onAddingListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog); //layout for dialog
                dialog.setTitle("Add a new Rule");
                dialog.setCancelable(false); //none-dismiss when touching outside Dialog

                // set the custom dialog components - texts and image
                spnTemp = (Spinner) dialog.findViewById(R.id.TempSpinner);
                spnOP = (Spinner) dialog.findViewById(R.id.OPSpinner);
                spnAmbient = (Spinner) dialog.findViewById(R.id.AmbientSpinner);
                spnBLind = (Spinner) dialog.findViewById(R.id.BlindSpinner);

                View btnAdd = dialog.findViewById(R.id.btn_ok);
                View btnCancel = dialog.findViewById(R.id.btn_cancel);

                //set spinner adapter
                ArrayList<String> tempList = new ArrayList<>();
                tempList.add("warm");
                tempList.add("hot");
                tempList.add("cold");
                tempList.add("comfort");
                tempList.add("freezing");
                tempList.add("null");
                ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, tempList);
                spnTemp.setAdapter(spnAdapter);

                //set spinner adapter
                ArrayList<String> opList = new ArrayList<>();
                opList.add("AND");
                opList.add("OR");
                ArrayAdapter<String> spnAdapter1 = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, opList);
                spnOP.setAdapter(spnAdapter1);

                //set spinner adapter
                ArrayList<String> ambList = new ArrayList<>();
                ambList.add("dim");
                ambList.add("dark");
                ambList.add("bright");
                ambList.add("null");
                ArrayAdapter<String> spnAdapter2 = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, ambList);
                spnAmbient.setAdapter(spnAdapter2);

                //set spinner adapter
                ArrayList<String> blindList = new ArrayList<>();
                blindList.add("open");
                blindList.add("close");
                blindList.add("half");
                blindList.add("null");
                ArrayAdapter<String> spnAdapter3 = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, blindList);
                spnBLind.setAdapter(spnAdapter3);


                //set handling event for 4 spinner 2 button
                spnTemp.setOnItemSelectedListener(onItemSelectedListener());
                spnOP.setOnItemSelectedListener(onItemSelectedListener());
                spnAmbient.setOnItemSelectedListener(onItemSelectedListener());
                spnBLind.setOnItemSelectedListener(onItemSelectedListener());

                btnAdd.setOnClickListener(onConfirmListener(dialog));
                btnCancel.setOnClickListener(onCancelListener(dialog));

                dialog.show();
            }
        };
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private View.OnClickListener onConfirmListener( final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String ruleText = "temp IS "+spnTemp.getSelectedItem().toString()+":"
                        +spnOP.getSelectedItem().toString()+" : "+"ambient IS "+spnAmbient.getSelectedItem().toString()+
                        ":"+"blind IS "+spnBLind.getSelectedItem().toString();*/
                String toSend = spnTemp.getSelectedItem().toString()+":"+
                        spnOP.getSelectedItem().toString()+":"+spnAmbient.getSelectedItem().toString()+":"+spnBLind.getSelectedItem().toString();

                RuleGetter rule = new RuleGetter(toSend);
                Log.d("debug", toSend);
                // send rules


                datatoSend = "RegisterRule,"+toSend;


                boolean flag = false;
                for(RuleGetter r :ruleArrayList){
                    String one = r.getName();
                    String two = rule.getName();
                    System.out.println(one+","+two);
                    if(one==two){
                        System.out.println("sameeeee");
                        flag = true;
                        continue;
                    }
                }
                if(flag) {
                    Toast.makeText(getActivity(),"Rule Already present",Toast.LENGTH_SHORT).show();
                }else{
                    //adding new object to arraylist
                    new SendJSONRequest().execute();
                    ruleArrayList.add(rule);
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        };
    }

    private View.OnClickListener onCancelListener(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }


    class SendJSONRequest extends AsyncTask<Void, String, String> {
        String response_txt;
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(Void... params) {
            String serverURL_text = "10.10.10.110:8080";
            //look into
            String request_method = datatoSend;
            datatoSend ="";
            Log.d("debug", "request is:"+request_method);
            response_txt = JSONHandler.testJSONRequest(serverURL_text, request_method);
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }
        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Log.d("debug", response_txt);
            if(result.contains("[")) {
                result = result.replace("raspberrypi[", "");
                String[] whole = result.split(",");
                Log.d("debug", "INSIDE");
                for (String s : whole) {
                    Log.d("debug", s);

                    String rule = s.replace(";", "").replace("[", "").replace("]","").replace("MIN:","").replace("BSUM:", "");
                    rule = rule.replace(" ","");
                    System.out.println(rule);
                    RuleGetter rule1 = new RuleGetter(rule);
                    ruleArrayList.add(rule1);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
