package test.rit.harsh.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public String datatoSend;
    private List<RuleGetter> rule;
    private Activity activity;

    public RecyclerAdapter(Activity activity, List<RuleGetter> rule) {
        this.rule = rule;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.recycler, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //setting data to view holder elements
        viewHolder.name.setText(rule.get(position).getName());

        //set on click listener for each element
        viewHolder.container.setOnClickListener(onClickListener(position));
    }

    private void setDataToView(TextView name, int position) {
        name.setText(rule.get(position).getName());
    }

    public void removeAt(int position) {
        this.rule.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != rule ? rule.size() : 0);
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewHolder newView = new ViewHolder(v);
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage("Are you sure,You wanted to delete this rule");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        datatoSend = "DeleteRule" + "," + newView.name.getText();
                        //((RuleFragment)newView.getAdapter()).removeItem(position);
                        System.out.println("inside delete view" + datatoSend);
                        removeAt(position);
                        new SendJSODeleteRequest().execute();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        };
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView job;
        private View container;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
            container = view.findViewById(R.id.card_view);
        }
    }


    class SendJSODeleteRequest extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverURL_text = "10.10.10.110:8080";
            //look into
            String request_method = datatoSend;
            datatoSend = "";
            Log.d("debug", "request is:" + request_method);
            response_txt = JSONHandler.testJSONRequest(serverURL_text, request_method);
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Log.d("debug", response_txt);
        }
    }

}

