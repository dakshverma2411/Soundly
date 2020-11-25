package daksh.soundly.ui;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import daksh.soundly.Adapters.SoundlyPlaylistAdapter;
import daksh.soundly.MainActivity;
import daksh.soundly.Model.Playlist;
import daksh.soundly.Model.Song;
import daksh.soundly.R;

public class Soundly extends Fragment {

    public static final String COLLECTION="collection_name";
    FirebaseDatabase firebaseDatabase;
    View rootView;
    RecyclerView recyclerViewSoundly;
    SoundlyPlaylistAdapter adapter;
    ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_soundly,container,false);
        firebaseDatabase=FirebaseDatabase.getInstance();
        progressBar=(ProgressBar) rootView.findViewById(R.id.soundly_progress_bar);
        DatabaseReference ref=firebaseDatabase.getReference().child("Collections");
        final ArrayList<Playlist> collections=new ArrayList<>();
        recyclerViewSoundly=(RecyclerView) rootView.findViewById(R.id.soundly_recycler_view);
        recyclerViewSoundly.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new SoundlyPlaylistAdapter(getContext(),collections);
        recyclerViewSoundly.setAdapter(adapter);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                String name=snapshot.getKey();
                int number=snapshot.getValue(Integer.class);
                collections.add(new Playlist(name,number));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }
}
