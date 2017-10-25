package mx.itesm.eibt.prototipoemprendimiento;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ethan on 12/10/2017.
 */

public class Industry extends LinearLayout {
    private int idIndustry;
    private String name;
    //private Drawable logo;
    private TextView eId;
    private TextView eName;
    private ImageButton eLogo;
    public Industry(Context context, int idIndustry, String name) {
        super(context);
        this.idIndustry = idIndustry;
        this.name = name;
        setStyle();
        createObjects(idIndustry, name);
    }

    private void createObjects(int idIndustry, String name) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(10,20,10,20);

        eLogo = new ImageButton(getContext());
        setLogo();

        eName = new TextView(getContext());
        setName(name);
        eName.setTextSize(20);
        eName.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        eName.setLayoutParams(params);
        addView(eName);

        eId = new TextView(getContext());
        setId(idIndustry);
        eId.setTextSize(20);
        eId.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        eId.setLayoutParams(params);
        addView(eId);
    }

    private void setStyle() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(20,20,20,20);
        setLayoutParams(params);
        setBackgroundResource(R.drawable.border);
    }
    public int getId(){return this.idIndustry;}
    public String getName(){
        return this.name;
    }
    //public Image getLogo(){return this.logo}

    public void setId(int id){
        this.idIndustry = id;
        eId.setText("" + id);
    }
    public void setName(String name){
        this.name = name;
        eName.setText("Nombre: " + name);
    }
    public void setLogo(){
        //this.logo = logo;
        eLogo.setImageResource(R.drawable.ecoinicio);
    }
}
