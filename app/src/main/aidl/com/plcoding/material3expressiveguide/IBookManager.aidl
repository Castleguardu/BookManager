package com.plcoding.material3expressiveguide;

import com.plcoding.material3expressiveguide.data.Book;
import com.plcoding.material3expressiveguide.INewBookArrivedListener;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void deleteBook(in Book book);
    void registerListener(INewBookArrivedListener listener);
    void unregisterListener(INewBookArrivedListener listener);
}
