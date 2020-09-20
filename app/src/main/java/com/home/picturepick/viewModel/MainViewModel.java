package com.home.picturepick.viewModel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/20 23:56
 * desc : viewmodel结合livedata使用
 * version : 1.0
 */
public class MainViewModel extends ViewModel {

    public MainViewModel(int countReserverd) {
        this.counter.setValue(countReserverd);
    }

    //public int count =0;换种方式
    public MutableLiveData<Integer> counter = new MutableLiveData<>();

    public void plusone() {
        int count = counter.getValue();
        counter.setValue(count + 1);
    }

    public void clear() {
        counter.setValue(0);
    }

}
