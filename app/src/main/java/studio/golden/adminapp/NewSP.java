package studio.golden.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewSP extends AppCompatActivity {


    @BindView(R.id.friend_list)
    RecyclerView friendList;
    ProgressBar progressBar;
    public static Uri mainImageURI = null;
    private FirebaseFirestore firebaseFirestore;
    CircleImageView imageView;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sp);
        progressBar = findViewById(R.id.progress_bar);

        ButterKnife.bind(this);
        init();
        getFriendList();
    }

    @SuppressLint("WrongConstant")
    private void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        friendList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }


    private void getFriendList(){
        Query query = db.collection("Users");

        FirestoreRecyclerOptions<FriendsResponse> response = new FirestoreRecyclerOptions.Builder<FriendsResponse>()
                .setQuery(query, FriendsResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(FriendsHolder holder, int position, FriendsResponse model) {
                progressBar.setVisibility(View.INVISIBLE);
                holder.textName.setText(model.getName());
                holder.textTitle.setText(String.format("%s, (%s)", model.getSub(), model.getService()));
                holder.textCompany.setText(String.format("%s, (%s)", model.getDistrict(), model.getState()));


                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference gsReference = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/service-provider/" + model.getService()+ model.getSub() + model.getState()+ model.getDistrict() + model.getUid() + ".jpg");


                gsReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(NewSP.this).load(uri.toString()).into(holder.imageView)).addOnFailureListener(exception -> {
                    // Handle any errors
                });

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(NewSP.this, SPShow.class);
                    intent.putExtra("uid", model.getLogin());

                    startActivity(intent);

                });
            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);
    }


    public class FriendsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView textName;
        @BindView(R.id.image)
        CircleImageView imageView;
        @BindView(R.id.title)
        TextView textTitle;
        @BindView(R.id.company)
        TextView textCompany;

        public FriendsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}


