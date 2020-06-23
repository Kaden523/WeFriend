package com.wefriend.wefriend.Main;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface OnGetDataListener<QuerySnapshot> {
    //public void onStart();
    //public void onSuccess(DataSnapshot data);
    //public void onFailed(DatabaseError databaseError);
    public void onComplete(Task<QuerySnapshot> task);
}