package ru.stepf.musicplayer;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

//TODO ускорить поиск
//TODO сделать фоном?
//TODO избавиться от лишней фигни(звуков)
public class Searcher {

    ListAdapter mListAdapter;
    Cursor mCursor;
    final Uri mediaSrc = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public Searcher(Context context)
    {
        String[] from = {MediaStore.MediaColumns.TITLE};
        int[] to = {android.R.id.text1};

        CursorLoader cursorLoader = new CursorLoader(context, mediaSrc, null, null, null, MediaStore.Audio.Media.TITLE);
        mCursor = cursorLoader.loadInBackground();

        mListAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, mCursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

    }

    public ListAdapter search(){
        return mListAdapter;
    }

    public String getName(int id){
        String _id;
        mCursor.moveToPosition(id);
        _id = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        return _id;
    }

    public Uri getPath(int id){
        String _id;
        mCursor.moveToPosition(id);
        _id = mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media._ID));

        return Uri.withAppendedPath(mediaSrc, _id);
    }
}
