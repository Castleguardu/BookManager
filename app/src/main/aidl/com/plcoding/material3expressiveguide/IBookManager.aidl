package com.plcoding.material3expressiveguide;

import com.plcoding.material3expressiveguide.data.Book;
import com.plcoding.material3expressiveguide.data.Note;
import com.plcoding.material3expressiveguide.INewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void deleteBook(in Book book);

    // Notes (OCR / reading session)
    List<Note> getNotesForBook(int bookId);
    void addNote(in Note note);
    void deleteNote(in Note note);

    void registerListener(INewBookArrivedListener listener);
    void unregisterListener(INewBookArrivedListener listener);
}
