package studio.golden.adminapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {

    View view;
    Context context;
    List<AdDetails> MainImageUploadInfoList;
    public static String sp_uid;
     static int totalCount;
    private int count;

    public AdsAdapter(Context context, List<AdDetails> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public AdsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_item, parent, false);

        AdsAdapter.ViewHolder viewHolder = new AdsAdapter.ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(AdsAdapter.ViewHolder holder, final int position) {

        final AdDetails studentDetails = MainImageUploadInfoList.get(position);

        holder.StudentNameTextView.setText(studentDetails.getName());

        holder.StudentNumberTextView.setText(String.valueOf(studentDetails.getPriority()));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        sp_uid = studentDetails.getUid();

        StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(String.format("gs://serviceapp-67984.appspot.com/ads_images/%s.jpg", String.valueOf(position + 1)));
        //StorageReference gsReference = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/service/Software Development.jpg");
        gsReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri.toString()).into(holder.image)).addOnFailureListener(exception -> {
            // Handle any errors
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditAd.class);
            intent.putExtra("uid", position + 1);
            context.startActivity(intent);
        });

        holder.imageView.setOnClickListener(v -> {

            DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("ShowAd/")
                    .child(String.valueOf(position + 1));
            mPostReference.removeValue();
            MainImageUploadInfoList.remove(position);
            notifyItemRemoved(position);
            count = position + 1;
            if (Ads.Abc > count){
                for(int i = 0; i < Ads.Abc - count; i++){
                    int finalI = i;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ShowAd/");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            databaseReference.child(String.valueOf(count + finalI)).setValue(dataSnapshot.child(String.valueOf(count + finalI + 1)).getValue());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                DatabaseReference PostReference = FirebaseDatabase.getInstance().getReference("ShowAd/")
                        .child(String.valueOf(Ads.Abc));
                PostReference.removeValue();
                Toast.makeText(context, "Ad Provider Deleted ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                context.startActivity(intent);
            }

        });
    }
    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    public void deleteItem(int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView StudentNameTextView;
        public TextView StudentNumberTextView;
        public CircleImageView image;
        public ImageView imageView;

        public ViewHolder(View itemView) {

            super(itemView);

            StudentNameTextView = itemView.findViewById(R.id.ShowStudentNameTextView);

            StudentNumberTextView = itemView.findViewById(R.id.ShowStudentNumberTextView);

            image = itemView.findViewById(R.id.abc);
            imageView = itemView.findViewById(R.id.trash);
        }
    }

    public void filterList(ArrayList<AdDetails> filterdNames) {
        this.MainImageUploadInfoList = filterdNames;
        notifyDataSetChanged();
    }

}


