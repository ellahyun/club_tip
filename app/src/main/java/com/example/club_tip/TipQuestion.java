package com.example.club_tip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.club_tip.Fragment.TipFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TipQuestion extends AppCompatActivity {


    RecyclerView recyclerView;
    EditText et_message;
    ImageView imv_send, tip_imv_leftarrow;
    List<Message> messageList;
    MessageAdapter messageAdapter;


    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_question);

        //연결시간 설정. 60초/120초/60초
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();


        recyclerView = findViewById(R.id.tip_rv1);
        et_message = findViewById(R.id.et_message);
        imv_send = findViewById(R.id.imv_send);


        // 돌아가는 왼쪽 이미지 버튼 눌렀을 때 페이지 돌아가기
        tip_imv_leftarrow = findViewById(R.id.tip_imv_leftarrow);
        tip_imv_leftarrow.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(), TipFragment.class);
            startActivity(intent);
        });




        // 메시지 초기화(사용자가 메시지를 보내면 메시지 목록이 비어있어야 하므로)
        messageList = new ArrayList<>();


        //RecyclerView
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        imv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_message.getText().toString().trim();
                addToChat(question, Message.SENT_BY_ME);
                et_message.setText("");
                callAPI(question);
            }
        });



    }



    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }




    void addResponse(String response) {
        messageList.remove(messageList.size()-1);
        addToChat(response, Message.SENT_BY_BOT);
    }





    // 답변을 기다리는 동안 "Typing.."으로 표시되도록 설정
    void callAPI(String question) {
        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));
        JSONObject jsonObject = new JSONObject();

        //추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        try {
            //AI 속성설정
            baseAi.put("role", "user");
            baseAi.put("content", "You are a helpful and kind AI Assistant.");
            //유저 메세지
            userMsg.put("role", "user");
            userMsg.put("content", question);
            //array로 담아서 한번에 보낸다
            arr.put(baseAi);
            arr.put(userMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            //모델명 변경
            object.put("model", "gpt-3.5-turbo");
            object.put("messages", arr);
//            아래 put 내용은 삭제하면 된다
//            object.put("model", "text-davinci-003");
//            object.put("prompt", question);
//            object.put("max_tokens", 4000);
//            object.put("temperature", 0);

        } catch (JSONException e){
            e.printStackTrace();
        }


        // okhttp 요청 생성
        RequestBody body = RequestBody.create (object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", " Bearer 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")
                .post(body)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Fail to load response due to " + e.getMessage());
            }


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful()) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject= new JSONObject(response.body().string());
                        jsonArray = jsonObject.getJSONArray("choices");

                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    addResponse("Failed to response due to " + response.body().string());
                }

            }
        });


    }
}
