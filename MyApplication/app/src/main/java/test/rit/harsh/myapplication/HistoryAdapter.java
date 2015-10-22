package test.rit.harsh.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> rule;
    private Activity activity;

    public HistoryAdapter(Activity activity, List<History> rule) {
        this.rule = rule;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.history_recycler, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //setting data to view holder elements
        viewHolder.temp.setText(rule.get(position).getTemp());
        viewHolder.date.setText(rule.get(position).getDatenTime());
        //set on click listener for each element
        viewHolder.container.setOnClickListener(onClickListener(position));
    }

    private void setDataToView(TextView name, int position) {
        name.setText(rule.get(position).getTemp());
    }

    @Override
    public int getItemCount() {
        return (null != rule ? rule.size() : 0);
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView temp;
        private TextView date;
        private TextView job;
        private View container;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            temp = (TextView) view.findViewById(R.id.tempHistory);
            date = (TextView) view.findViewById(R.id.dateHistory);
            container = view.findViewById(R.id.card_view);
        }
    }
}