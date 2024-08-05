package br.edu.ifsp.dpdm.exemploservices.modelo;

import org.json.JSONException;
import org.json.JSONObject;
public class DisneyCharacter {

    private String nome;
    private String image;

    public DisneyCharacter(JSONObject json) throws JSONException {
        this.nome = json.getString("name");
        this.image = json.getString("imageUrl");
    }

    public DisneyCharacter() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
