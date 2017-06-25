package io.github.akshayadinesh.ifeel;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Calendar;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class FirstFragment extends Fragment {

    Button button;
    EditText inputText;
    TextView date;
    TextView content;
    ProgressBar angerp;
    ProgressBar disgustp;
    ProgressBar fearp;
    ProgressBar joyp;
    ProgressBar sadnessp;
    LinearLayout linearLayout;
    LinearLayout line;


    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);

        button = (Button) view.findViewById(R.id.submitButton);
        inputText = (EditText) view.findViewById(R.id.editText);
        date = (TextView) view.findViewById(R.id.date);
        content = (TextView) view.findViewById(R.id.entry);
        angerp = (ProgressBar) view.findViewById(R.id.anger);
        disgustp = (ProgressBar) view.findViewById(R.id.disgust);
        fearp = (ProgressBar) view.findViewById(R.id.fear);
        joyp = (ProgressBar) view.findViewById(R.id.joy);
        sadnessp = (ProgressBar) view.findViewById(R.id.sadness);
        linearLayout = (LinearLayout) view.findViewById(R.id.hiddenLinearLayout);
        line = (LinearLayout) view.findViewById(R.id.hiddenLine);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                double[] emo = makeGetRequest(inputText.getText().toString());

                String display = "Anger: "+emo[0];
                display += "\n Disgust: "+emo[1];
                display += "\n Fear: "+emo[2];
                display += "\n Joy: "+emo[3];
                display += "\n Sadness: "+emo[4];

                System.out.println(display);

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH)+1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                date.setText("Date: "+month+"/"+day+"/"+year);
                content.setText(inputText.getText().toString());
                angerp.setProgress((int)(emo[0]*100));
                disgustp.setProgress((int)(emo[1]*100));
                fearp.setProgress((int)(emo[2]*100));
                joyp.setProgress((int)(emo[3]*100));
                sadnessp.setProgress((int)(emo[4]*100));
                linearLayout.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public double[] makeGetRequest(String input) {

        try {

            String query = URLEncoder.encode(input, "utf-8");

            String url = "https://watson-api-explorer.mybluemix.net/tone-analyzer/api/v3/tone?version=2016-05-19&text="+query;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);

//          add request header

            HttpResponse response;
            request.addHeader("User-Agent", USER_AGENT);
            response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            JSONObject json = new JSONObject(result.toString());
            JSONArray categories = json.getJSONObject("document_tone").getJSONArray("tone_categories");
            JSONArray tones = categories.getJSONObject(0).getJSONArray("tones");
            double anger = Double.valueOf(tones.getJSONObject(0).getString("score"));
            double disgust = Double.valueOf(tones.getJSONObject(1).getString("score"));
            double fear = Double.valueOf(tones.getJSONObject(2).getString("score"));
            double joy = Double.valueOf(tones.getJSONObject(3).getString("score"));
            double sadness = Double.valueOf(tones.getJSONObject(4).getString("score"));

            double[] results = {anger, disgust, fear, joy, sadness};

            return results;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
