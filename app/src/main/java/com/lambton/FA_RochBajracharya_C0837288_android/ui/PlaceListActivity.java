package com.lambton.FA_RochBajracharya_C0837288_android.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lambton.FA_RochBajracharya_C0837288_android.R;
import com.lambton.FA_RochBajracharya_C0837288_android.db.DatabaseClient;
import com.lambton.FA_RochBajracharya_C0837288_android.db.PlaceDao;
import com.lambton.FA_RochBajracharya_C0837288_android.model.Place;
import com.lambton.FA_RochBajracharya_C0837288_android.ui.adapter.ListAdapter;
import com.lambton.FA_RochBajracharya_C0837288_android.util.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity {
    RecyclerView rvList;
    ListAdapter listAdapter;

    List<Place> placeList = new ArrayList<>();
    private PlaceDao placeDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initializeToolbar();
        initialize();
    }

    void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bookmarked Places");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initialize() {
        rvList = findViewById(R.id.recycler_view_list);
        listAdapter = new ListAdapter(this, placeList);

        placeDao = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().placeDao();

        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvList.setAdapter(listAdapter);


    }

    private void makeList() {
        placeList.clear();
        placeList.addAll(placeDao.getAllPlaces());

        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeList();
        enableSwipeToDeleteAndUndo();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                listAdapter.removeItem(position);

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvList);
    }
}
