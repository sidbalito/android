package com.sidbatista.relationalpmbok5;

import android.app.*;
import android.os.*; 
import android.view.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.database.*;

public class MainActivity extends Activity implements ItensList
{
	ViewGroup vg;
	QueryBuilder builder;
	private boolean root;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//startActivity(new Intent(this, ItensActivity.class));
		vg = (ViewGroup)findViewById(R.id.main);
		categorias();
		builder = QueryBuilder.newInstance(this);
    }

	@Override
	protected void onResume()
	{
		QueryBuilder.newInstance(this);
		super.onResume();
	}
	
	private void categorias(){
		root = true;
		vg.removeAllViews();
		vg.addView(new ItemTextView(this, "Grupos", new Component(0, Component.GRUPO)));
		vg.addView(new ItemTextView(this, "Areas", new Component(0, Component.AREA)));
	}
	
	
	private void teste(){
		new Thread(new Runnable(){
			public void run(){
				try{
					runTeste();
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}).start();
	}
	
	private void runTeste() throws Exception{
		int i = 0;
		for(i = 0; i < 50; i++){
			startActivity(new Intent(this, ItensActivity.class));
			Log.i("Iterações", "iteração "+i);
			Thread.sleep(500);
		}
		Toast.makeText(this, i+" iterações", 1000);
		Log.i("Iterações", i+" iterações");
	} 
	
	public void listaItens(Component component){
		if(component.getIndex() > 0){
			Intent intent = new Intent(this, ItensActivity.class);
			Log.i("startActivity", component.toString());
			intent.putExtra(Component.TYPE, component.getType());
			intent.putExtra(Component.INDEX, component.getIndex());
			startActivity(intent);
		} else {
			root = false;
			vg.removeAllViews();
			Log.i("MainActivity.listaItens", component.toString());
			builder.runQuery(component.getType());
		}
	}
	
	public void onRecord(Cursor cursor, Component component){
		Log.i("onRecord", cursor.getInt(1)+"");
		vg.addView(new ItemTextView(this, cursor.getString(0), new Component(cursor.getInt(1), component.getType())));
	}

	@Override
	public void onBackPressed() {
		if (!root) {
			categorias();
			return;
		}

		// Otherwise defer to system default behavior.
		super.onBackPressed();
	}
		
	public void afterLastRecord(){
		
	}
}
