package tipkuu.padi.com.tipkuu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tipkuu.padi.com.tipkuu.R;
import tipkuu.padi.com.tipkuu.models.Event;

import static android.content.ContentValues.TAG;

class EventViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

    private final ImageView profileImage;
    private final Context context;
    private final TextView tipAmt;
    private final TextView name;

    public EventViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.name = (TextView)itemView.findViewById(R.id.name);
        this.profileImage = (ImageView)itemView.findViewById(R.id.tipperProfileImage);
        this.tipAmt = (TextView)itemView.findViewById(R.id.tip_amount);
    }

    @Override
    public void onClick(View view) {

    }

    public void setEvent(Event event) {
        String photoUrl = event.getTipper().getPhotoUrl();
        Log.d(TAG, ">>>>>>>>>>> photoUrl = " + photoUrl);
        Picasso.with(context).load(event.getTipper().getPhotoUrl()).into(this.profileImage);
        this.name.setText(event.getTipper().getName());
        this.tipAmt.setText(event.getAmount());
    }
}
