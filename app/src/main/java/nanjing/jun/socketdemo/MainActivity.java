package nanjing.jun.socketdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SocketThread.OnClientListener {

    private SocketThread socketThread;
    private StringBuilder stringBuilder = new StringBuilder();
    private TextView serviceTv;
    private EditText contentEt;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceTv = (TextView) findViewById(R.id.tv_service);
        contentEt = (EditText) findViewById(R.id.et_content);
        sendBtn = (Button) findViewById(R.id.btn_send);
        socketThread = new SocketThread(this);
        socketThread.start();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringBuilder.append("我:\n");
                stringBuilder.append(contentEt.getText().toString());
                stringBuilder.append("\n");
                serviceTv.setText(stringBuilder.toString());
                socketThread.sendMessage(contentEt.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketThread.disconnect();
    }

    @Override
    public void onNewMessage(String msg) {
        Log.i("收到的信息i",msg);
        String[] s = msg.split("#");
        stringBuilder.append(s[0]);
        stringBuilder.append("\n");
        stringBuilder.append(s[1]);
        stringBuilder.append("\n");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceTv.setText(stringBuilder.toString());
            }
        });
    }
}
