package nanjing.jun.socketdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SocketThread socketThread;
    private StringBuilder stringBuilder = new StringBuilder();
    private TextView serviceTv;
    private EditText contentEt;
    private Button sendBtn;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String message = (String) msg.obj;
                Log.i("收到的信息i", message);
                String[] s = message.split("#");
                stringBuilder.append(s[0]);
                stringBuilder.append("\n");
                stringBuilder.append(s[1]);
                stringBuilder.append("\n");
                serviceTv.setText(stringBuilder.toString());
            } else if (msg.what == 2) {
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(MainActivity.this, "连接断开", Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceTv = (TextView) findViewById(R.id.tv_service);
        contentEt = (EditText) findViewById(R.id.et_content);
        sendBtn = (Button) findViewById(R.id.btn_send);
        socketThread = new SocketThread(mHandler);
        socketThread.start();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringBuilder.append("我:\n");
                stringBuilder.append(contentEt.getText().toString());
                stringBuilder.append("\n");
                serviceTv.setText(stringBuilder.toString());
                socketThread.sendMessage(contentEt.getText().toString());
                contentEt.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketThread.disconnect();
    }

}
