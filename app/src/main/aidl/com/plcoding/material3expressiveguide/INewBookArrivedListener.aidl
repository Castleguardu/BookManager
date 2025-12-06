package com.plcoding.material3expressiveguide;

import com.plcoding.material3expressiveguide.data.Book;

interface INewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
