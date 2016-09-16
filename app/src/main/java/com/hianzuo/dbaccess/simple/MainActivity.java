package com.hianzuo.dbaccess.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hianzuo.dbaccess.DBInterface;
import com.hianzuo.dbaccess.simple.dao.User;
import com.hianzuo.viewinject.Injector;
import com.hianzuo.viewinject.ViewHolder;
import com.hianzuo.viewinject.ViewInjectClick;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Holder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holder = Injector.inject(this, Holder.class);
    }

    public void on_btn_save_click() {
        User lucy4 = new User("lucy4", 18);
        lucy4.setCategory("cate1");
        DBInterface.saveOrUpdate(lucy4);
        lucy4.setCategory("cate2");
        DBInterface.saveOrUpdate(new User("lucy2", 19));
        holder.tv_result.setText("Save Success.");
    }

    public void on_btn_query_click() {
        List<User> users = DBInterface.readAll(User.class);
        String result = "Result:";
        for (User user : users) {
            result += user.toString();
        }
        holder.tv_result.setText(result);
    }

    private static class Holder implements ViewHolder {
        @ViewInjectClick
        private Button btn_save;
        @ViewInjectClick
        private Button btn_query;
        private TextView tv_result;
    }
}
