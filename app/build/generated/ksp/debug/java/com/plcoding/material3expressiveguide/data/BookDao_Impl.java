package com.plcoding.material3expressiveguide.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BookDao_Impl implements BookDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Book> __insertionAdapterOfBook;

  private final EntityDeletionOrUpdateAdapter<Book> __deletionAdapterOfBook;

  public BookDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBook = new EntityInsertionAdapter<Book>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `books` (`id`,`title`,`author`,`price`,`coverUri`,`description`,`rating`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Book entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthor());
        statement.bindDouble(4, entity.getPrice());
        if (entity.getCoverUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCoverUri());
        }
        statement.bindString(6, entity.getDescription());
        statement.bindDouble(7, entity.getRating());
        statement.bindLong(8, entity.getStatus());
      }
    };
    this.__deletionAdapterOfBook = new EntityDeletionOrUpdateAdapter<Book>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `books` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Book entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public void insert(final Book book) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfBook.insert(book);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Book book) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfBook.handle(book);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Book> getAll() {
    final String _sql = "SELECT * FROM books";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
      final int _cursorIndexOfCoverUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUri");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<Book> _result = new ArrayList<Book>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Book _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpTitle;
        _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        final String _tmpAuthor;
        _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
        final double _tmpPrice;
        _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
        final String _tmpCoverUri;
        if (_cursor.isNull(_cursorIndexOfCoverUri)) {
          _tmpCoverUri = null;
        } else {
          _tmpCoverUri = _cursor.getString(_cursorIndexOfCoverUri);
        }
        final String _tmpDescription;
        _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        final float _tmpRating;
        _tmpRating = _cursor.getFloat(_cursorIndexOfRating);
        final int _tmpStatus;
        _tmpStatus = _cursor.getInt(_cursorIndexOfStatus);
        _item = new Book(_tmpId,_tmpTitle,_tmpAuthor,_tmpPrice,_tmpCoverUri,_tmpDescription,_tmpRating,_tmpStatus);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
