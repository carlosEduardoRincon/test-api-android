package br.edu.ifsp.dpdm.exemploservices;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import br.edu.ifsp.dpdm.exemploservices.modelo.DisneyCharacter;

public class MainActivity extends AppCompatActivity {


    private EditText edNomePesonagem;
    private TextView txtDadosPersonagem;
    private DisneyCharacter character;
    private ImageView imgPersonagem;
    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personagem);
        edNomePesonagem = (EditText) findViewById(R.id.edNomePesonagem);
        txtDadosPersonagem = (TextView) findViewById(R.id.txtDadosPersonagem);
        imgPersonagem = (ImageView) findViewById(R.id.imgPersonagem);
    }

    public void pesquisar(View v) {
        new FetchCharacterTask().execute(edNomePesonagem.getText().toString());
    }

    public class FetchCharacterTask extends AsyncTask<String, Void, DisneyCharacter> {
        private static final String TAG = "FetchPCharacterTask";

        protected void onPreExecute() {
            load = ProgressDialog.show(MainActivity.this, "Por favor Aguarde ...",
                    "Procurando Dados ...");
        }

        @Override
        protected DisneyCharacter doInBackground(String... params) {

            String nomePersonagem = params[0].toLowerCase();
            String urlString = "https://api.disneyapi.dev/character?name=" + nomePersonagem;
            try {
                String jsonString = HttpUtils.get(urlString);
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
                return new DisneyCharacter(data);
            } catch (Exception e) {
                Log.e(TAG, "Erro na requisição: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(DisneyCharacter disneyCharacter) {

            if(disneyCharacter !=null)
            {
                Toast.makeText(MainActivity.this,String.valueOf(disneyCharacter.getNome()),Toast.LENGTH_LONG).show();
                txtDadosPersonagem.setText(disneyCharacter.getNome());
                if(disneyCharacter.getImage()!=null)
                {
                    new DownloadImageTask(imgPersonagem)
                            .execute(disneyCharacter.getImage());
                }

            }
            else {
                Toast.makeText(MainActivity.this,"Nenhum personagem encontrado!",Toast.LENGTH_LONG).show();
                txtDadosPersonagem.setText("");
                imgPersonagem.setImageDrawable(null);
            }



            load.dismiss();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}