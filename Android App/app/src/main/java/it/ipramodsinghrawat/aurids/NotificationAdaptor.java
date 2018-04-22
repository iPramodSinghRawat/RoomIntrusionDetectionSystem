package it.ipramodsinghrawat.aurids;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotificationAdaptor extends RecyclerView.Adapter<NotificationAdaptor.MyViewHolder> {

    private List<IntrusionNotification> intrusionNotificationsList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView notificationTV, timeTV;

        public MyViewHolder(View view) {
            super(view);
            notificationTV = (TextView) view.findViewById(R.id.notificationTV);
            timeTV = (TextView) view.findViewById(R.id.timeTV);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("ListView Tag", "onClick " + getPosition()+" "+getAdapterPosition()+" "+getLayoutPosition());
            Toast.makeText(view.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();

            IntrusionNotification intrusionNotification = intrusionNotificationsList.get(getAdapterPosition());

            Intent intent = new Intent(context, IntrusionDetail.class);
            intent.putExtra("key", intrusionNotification.key);
            intent.putExtra("details", intrusionNotification.details);
            intent.putExtra("faceFile", intrusionNotification.faceFile);
            intent.putExtra("faceFrameFile", intrusionNotification.faceFrameFile);
            intent.putExtra("notification", intrusionNotification.notification);
            intent.putExtra("timestamp", intrusionNotification.timestamp);
            context.startActivity(intent);
        }
    }

    public NotificationAdaptor(List<IntrusionNotification> intrusionNotificationsList, Context context) {
        this.intrusionNotificationsList = intrusionNotificationsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_view_row, parent, false);
            return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        IntrusionNotification intrusionNotification = intrusionNotificationsList.get(position);
        holder.notificationTV.setText(intrusionNotification.notification);
        holder.timeTV.setText(intrusionNotification.timestamp);

    }

    @Override
    public int getItemCount() {
        return intrusionNotificationsList.size();
    }

    public IntrusionNotification getItem(int position){
        return intrusionNotificationsList.get(position);
    }
}