package br.edu.ifsp.dpdm.exemploservices;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private MinionService ps;
    private EditText initialText;
    private TextView translatedText;
    private String translated;
    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon);
        initialText = (EditText) findViewById(R.id.edNomePokemon);
        translatedText = (TextView) findViewById(R.id.txtDadosPokemon);
    }

    public void pesquisar(View v) {
        ps = new MinionService();
        ps.execute(initialText.getText().toString());
    }

    private class MinionService extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(MainActivity.this, "Por favor Aguarde ...",
                    "Procurando Dados ...");
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {

                String initialString = params[0].toLowerCase();
                URL url = new URL("https://api.funtranslations.com/translate/minion.json?text="+initialString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));


                String linha;
                StringBuffer buffer = new StringBuffer();
                while((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                    buffer.append("\n");
                }
                translated = convertJsonToObject(buffer.toString());

                return translated;
            } catch (Exception e) {
                e.printStackTrace();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return null;
        }



        @Override
        protected void onPostExecute(String dados) {
            if(dados !=null)
            {
                Toast.makeText(MainActivity.this,String.valueOf(dados),Toast.LENGTH_LONG).show();
                translatedText.setText(dados);
            }

            load.dismiss();
        }
    }


    public String convertJsonToObject(String dados)
    {
        String translated = null;

        try {

            JSONObject jsonObj = new JSONObject(dados);
            if(jsonObj !=null)
            {
                JSONObject contents = jsonObj.getJSONObject("contents");
                translated = contents.getString("translated");
            }
            else {
                translated = null;
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return translated;

    }
}