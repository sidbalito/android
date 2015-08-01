package com.sidbatista.relationalpmbok5;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import android.util.*;
import android.content.*;

public class ItemTextView extends TextView implements OnClickListener
{ 
	private Component component;
	private ItensList context;
	
	public ItemTextView(ItensList context, String text, Component component){
		super((Context)context);
		this.context = context;
		setText(text);
		this.component = component;
		setOnClickListener(this);
	}
	
	public void onClick(View view){
		if(component != null){
			Log.i("onClick", component.toString());
			context.listaItens(component);
		}
	}
}
