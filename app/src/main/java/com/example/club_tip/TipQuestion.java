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





    void callAPI(String question) {
        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", question);
            messageArr.put(obj);

            jsonObject.put("messages", messageArr);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        // JSON 데이터
        String JSON = "{\n" +
                "  \"model_id\": \"ibm/mpt-7b-instruct\",\n" +
                "  \"inputs\": [\n" +
                "    \"\"\n" +
                "  ],\n" +
                "  \"parameters\": {\n" +
                "    \"decoding_method\": \"greedy\",\n" +
                "    \"min_new_tokens\": 1,\n" +
                "    \"max_new_tokens\": 200\n" +
                "  }\n" +
                "}";


        // okhttp 요청 생성
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://workbench-api.res.ibm.com/v1/generate")
                .header("Authorization", " Bearer pak-Yhyf1YwmFkyArKEn5LwZh8bXqIGWwG3UEhoZX3jhMU4")
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
                    try {
                        JSONObject jsonObject= new JSONObject(response.body().string());
                        JSONArray jsonArray = null;
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
