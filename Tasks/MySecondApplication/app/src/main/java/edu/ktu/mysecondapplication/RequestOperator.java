package edu.ktu.mysecondapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestOperator extends Thread {

    public interface RequestOperatorListener{
        void success (List<ModelPost> publication);
        void failed (int responseCode);
        void loading();
    }

    private RequestOperatorListener listener;
    private int responseCode;
    private List <ModelPost> list;

    public void setListener (RequestOperatorListener listener){
        this.listener = listener;
        this.list = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();
        loading();
        try{
            List<ModelPost> publication = request();
            if(publication != null){
                success(publication);
            }else{
                failed(responseCode);
            }
        }catch(IOException E){
            failed(-1);
        }catch (JSONException e){
            failed(-2);
        }
    }

    private List<ModelPost> request() throws IOException, JSONException {
        //URL address
        URL obj = new URL("http://jsonplaceholder.typicode.com/posts");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type","application/json");

        responseCode = con.getResponseCode();

        System.out.println("Response Code" + responseCode);

        InputStreamReader streamReader;

        if(responseCode == 200){
            streamReader = new InputStreamReader(con.getInputStream());
        } else {
            streamReader = new InputStreamReader(con.getErrorStream());
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer response = new StringBuffer();
        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                sleep(4);
            }
            in.close();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

       // System.out.println(response.toString());

        if(responseCode == 200){
            return parsingJsonObject(response.toString());
        } else{
            return null;
        }
    }

    public List<ModelPost> parsingJsonObject(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        for (int x = 0; x< array.length(); x++ ){
            JSONObject object = array.optJSONObject(x);
            ModelPost post = new ModelPost();

            post.setId(object.optInt("id",0));
            post.setUserId(object.optInt("userId",0));

            post.setTitle(object.getString("title"));
            post.setBodyText(object.getString("body"));

            list.add(post);
        }
        return list;
    }

    private void failed(int code){
        if(listener != null){
            listener.failed(code);
        }
    }

    private void success(List<ModelPost> publication){
        if(listener != null){
            listener.success(publication);
        }
    }

    private void loading(){
        if (listener != null){
            listener.loading();
        }
    }

}
