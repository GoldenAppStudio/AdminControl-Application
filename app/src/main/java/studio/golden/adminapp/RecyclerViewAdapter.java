package studio.golden.adminapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract{

    View view;
    Context context;
    List<ServiceProviderClass> MainImageUploadInfoList;
    public static String sp_uid;
    private ServiceProviderClass serviceProviderClass;
    private int totalCount;
    private int count;

    public RecyclerViewAdapter(Context context, List<ServiceProviderClass> TempList) {

        this.MainImageUploadInfoList = TempList;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ServiceProviderClass studentDetails = MainImageUploadInfoList.get(position);
        serviceProviderClass = studentDetails;
        holder.StudentNameTextView.setText(studentDetails.getName());

        holder.StudentNumberTextView.setText(studentDetails.getPhone());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference gsReference = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/service-provider/" +Chooser.key+ Chooser.sub_service + "" + Chooser.province + "" + Arrays.toString(Chooser.county) + "" + studentDetails.getUid() + ".jpg");
        Toast.makeText(context, studentDetails.getUid(), Toast.LENGTH_LONG).show();

        gsReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri.toString()).into(holder.image)).addOnFailureListener(exception -> {
            StorageReference reference = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/service/sub-service/"+ Chooser.key + "/" + Chooser.sub_service +".jpg");
            reference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context).load(uri.toString()).into(holder.image)).addOnFailureListener(e -> {});
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Chooser.linkX + "/");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    totalCount = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SPMain.class);
            sp_uid = studentDetails.getUid();
            intent.putExtra("uid", studentDetails.getUid());
            context.startActivity(intent);
            Toast.makeText(context, sp_uid, Toast.LENGTH_SHORT).show();
        });

        holder.imageView.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Are you sure?")
                    .setMessage("If you proceed then it will be permanently deleted. It can not be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Proceed", (dialog, which) -> {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Chooser.linkX + "/" + studentDetails.getUid());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeValue();
                                MainImageUploadInfoList.remove(position);
                                notifyItemRemoved(position);
                               // nofityDataChanged();
                                notifyItemRangeChanged(position, MainImageUploadInfoList.size());
                                Toast.makeText(context, "Service Provider Deleted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        count = Integer.parseInt(studentDetails.getUid());
                        if (totalCount > count){
                            for(int i = 0; i < totalCount - count; i++){
                                int finalI = i;

                                db.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        db.child(String.valueOf(count + finalI)).setValue(dataSnapshot.child(String.valueOf(count + finalI + 1)).getValue());
                                        db.child(String.valueOf(count + finalI)).child("uid").setValue(String.valueOf(count + finalI));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            DatabaseReference PostReference = FirebaseDatabase.getInstance().getReference(Chooser.linkX + "/")
                                    .child(String.valueOf(totalCount));
                            PostReference.removeValue();

                        }

                    }).create().show();
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

    public void filterList(ArrayList<ServiceProviderClass> filterdNames) {
        this.MainImageUploadInfoList = filterdNames;
        notifyDataSetChanged();
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(MainImageUploadInfoList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(MainImageUploadInfoList, i, i - 1);
            }
        }
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Chooser.linkX + "/" + String.valueOf(fromPosition + 1));
        db.child("priority").setValue(String.valueOf(serviceProviderClass.getPriority() + 1));
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }

}



