package com.sidbatista.relationalpmbok5;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*; 
import android.database.*;
import java.util.*; 
import android.util.*;
import android.graphics.*;

public class ItensActivity extends Activity implements ItensList
{
	private ViewGroup itens;
	private Stack<Component> componentStack = new Stack<Component>();
	private Component currComponent;
	private QueryBuilder builder;
	private boolean firstRecord;
	private HashMap<String, ViewGroup> viewGroups = new HashMap<String, ViewGroup>();
	private int
	grupoCol, areaCol, processoCol, artefatoCol,
	grupoIdCol, areaIdCol, processoIdCol, artefatoIdCol, aplicacaoIdCol;
	
	private static final String[] APLICACOES = new String[]{
		"Entradas:",
		"Ferramentas:",
		"Saidas:"
	};
	
	private static final String[] APLICACOES_ARTEFATO = new String[]{
		"Entrada para:",
		"Ferramenta de:",
		"Saida de:"
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		//QueryBuilder.resToFile(this);
        setContentView(R.layout.itens);
		itens = (ViewGroup) findViewById(R.id.itens);
		currComponent = new Component(1, Component.GRUPO);
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		Intent intent = getIntent();
		int type = intent.getIntExtra(Component.TYPE, 0);
		if(type != 0){
			currComponent = new Component(intent.getIntExtra(Component.INDEX, 0), type);
		}

		if(builder == null)
			builder = QueryBuilder.newInstance(this);
		else
			Log.i("onCreate", "QueryBuilder já existe");
		if(currComponent == null)
			currComponent = new Component(1, Component.GRUPO);
		listaItens();
	}

	@Override
	protected void onPause()
	{
		builder.release();
		builder = null;
		super.onPause();
	}
	
	private void setTitle(String title){
		//actionBar.setTitle(title);
		TextView tv = new TextView(this);
		tv.setText(title);
		tv.setTypeface(null, Typeface.BOLD);
		itens.addView(tv);
	}
	
	private void listaItens(){
		itens.removeAllViews();
		currComponent.getIndex();
		Log.i("listaItens", currComponent.toString());
		firstRecord = true;
		builder.runQuery(currComponent);
	}
	
	public void listaItens(Component component){
		componentStack.add(currComponent);
		currComponent = component;
		listaItens();
	}
	
	public void onRecord(Cursor cursor, Component component){
		if(firstRecord){
			areaCol = builder.getAreaCol();
			grupoCol = builder.getGrupoCol();
			processoCol = builder.getProcessoCol();
			artefatoCol = builder.getArtefatoCol();
			areaIdCol = builder.getAreaIdCol();
			grupoIdCol = builder.getGrupoIdCol();
			processoIdCol = builder.getProcessoIdCol();
			artefatoIdCol = builder.getArtefatoIdCol();
			aplicacaoIdCol = builder.getAplicacaoIdCol();
		}
		switch(component.getType()){
			case Component.AREA:
				mostraArea(cursor);
				break;
			case Component.GRUPO:
				mostraGrupo(cursor);
				break;
			case Component.PROCESSO:
				mostraProcesso(cursor);
				break;
			case Component.ARTEFATO:
				mostraArtefato(cursor);
				break;
		}
	}
	
	public void afterLastRecord(){
		viewGroups.clear();
	}
	
	private void mostraArea(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			Log.i("mostraArea", cursor.getString(areaCol));
			setTitle(cursor.getString(areaCol));
			//itens.addView(new ItemTextView(this, cursor.getString(areaCol), null));
		}
		String strGrupo = cursor.getString(grupoCol);
		ViewGroup viewGroup = getGroup(strGrupo, cursor, Component.GRUPO, grupoIdCol);
		TextView tv = new ItemTextView(this, cursor.getString(processoCol), new Component(cursor.getInt(processoIdCol), Component.PROCESSO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}

	private ViewGroup getGroup(String strGrupo)
	{
		LinearLayout viewGroup = (LinearLayout) viewGroups.get(strGrupo);
		if (viewGroup == null)
		{
			itens.addView(new ItemTextView(this, strGrupo, null));
			viewGroup = new LinearLayout(this);
			viewGroup.setOrientation(LinearLayout.VERTICAL);
			viewGroups.put(strGrupo, viewGroup);
			itens.addView(viewGroup);
		}
		return viewGroup;
	}

	private ViewGroup getGroup(String strGrupo, Cursor cursor, int tipo, int idCol)
	{
		LinearLayout viewGroup = (LinearLayout) viewGroups.get(strGrupo);
		if (viewGroup == null)
		{
			itens.addView(new ItemTextView(this, strGrupo, new Component(cursor.getInt(idCol), tipo)));
			viewGroup = new LinearLayout(this);
			viewGroup.setOrientation(LinearLayout.VERTICAL);
			viewGroups.put(strGrupo, viewGroup);
			itens.addView(viewGroup);
		}
		return viewGroup;
	}
	
	private void mostraGrupo(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			setTitle(cursor.getString(grupoCol));
			//itens.addView(new ItemTextView(this, cursor.getString(grupoCol), null));
		}
		String strArea = cursor.getString(areaCol);
		ViewGroup viewGroup = getGroup(strArea, cursor, Component.AREA, areaIdCol);
		TextView tv = new ItemTextView(this, cursor.getString(processoCol), new Component(cursor.getInt(processoIdCol), Component.PROCESSO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}
	
	private void mostraProcesso(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			setTitle(cursor.getString(processoCol));
			//itens.addView(new ItemTextView(this, cursor.getString(processoCol), null));
			itens.addView(new ItemTextView(this, cursor.getString(areaCol), new Component(cursor.getInt(areaIdCol), Component.AREA)));
			itens.addView(new ItemTextView(this, cursor.getString(grupoCol), new Component(cursor.getInt(grupoIdCol), Component.GRUPO)));
		}
		String strAplicacao = APLICACOES[ cursor.getInt(aplicacaoIdCol)-1];
		ViewGroup viewGroup = getGroup(strAplicacao);
		TextView tv = new ItemTextView(this, cursor.getString(artefatoCol), new Component(cursor.getInt(artefatoIdCol), Component.ARTEFATO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}
	
	private void mostraArtefato(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			setTitle(cursor.getString(artefatoCol));
			//itens.addView(new ItemTextView(this, cursor.getString(artefatoCol), null));
		}
		String strAplicacao = APLICACOES_ARTEFATO[ cursor.getInt(aplicacaoIdCol)-1];
		Log.i("Artefato", strAplicacao);
		ViewGroup viewGroup = getGroup(strAplicacao);
		TextView tv = new ItemTextView(this, cursor.getString(processoCol), new Component(cursor.getInt(processoIdCol), Component.PROCESSO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}
	
	@Override
	public void onBackPressed() {
		if (!componentStack.isEmpty()) {
			currComponent = componentStack.pop();
			listaItens();
			return;
		}

		// Otherwise defer to system default behavior.
		super.onBackPressed();
	}
}
package com.sidbatista.relationalpmbok5;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*; 
import android.database.*;
import java.util.*; 
import android.util.*;
import android.graphics.*;

public class ItensActivity extends Activity implements ItensList
{
	private ViewGroup itens;
	private Stack<Component> componentStack = new Stack();
	private Component currComponent;
	private QueryBuilder builder;
	private boolean firstRecord;
	private ActionBar actionBar;
	private HashMap<String, ViewGroup> viewGroups = new HashMap();
	private int
	grupoCol, areaCol, processoCol, artefatoCol,
	grupoIdCol, areaIdCol, processoIdCol, artefatoIdCol, aplicacaoIdCol;
	
	private static final String[] APLICACOES = new String[]{
		"Entradas:",
		"Ferramentas:",
		"Saidas:"
	};
	
	private static final String[] APLICACOES_ARTEFATO = new String[]{
		"Entrada para:",
		"Ferramenta de:",
		"Saida de:"
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		//QueryBuilder.resToFile(this);
        setContentView(R.layout.itens);
		itens = (ViewGroup) findViewById(R.id.itens);
		currComponent = new Component(1, Component.GRUPO);
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		Intent intent = getIntent();
		int type = intent.getIntExtra(Component.TYPE, 0);
		if(type != 0){
			currComponent = new Component(intent.getIntExtra(Component.INDEX, 0), type);
		}

		if(builder == null)
			builder = QueryBuilder.newInstance(this);
		else
			Log.i("onCreate", "QueryBuilder já existe");
		if(currComponent == null)
			currComponent = new Component(1, Component.GRUPO);
		listaItens();
	}

	@Override
	protected void onPause()
	{
		builder.release();
		builder = null;
		super.onPause();
	}
	
	
	
	private void add(View view){
		itens.addView(view);
	}
	
	private void setTitle(String title){
		//actionBar.setTitle(title);
		TextView tv = new TextView(this);
		tv.setText(title);
		tv.setTypeface(null, Typeface.BOLD);
		itens.addView(tv);
	}
	
	private void listaItens(){
		itens.removeAllViews();
		int index = currComponent.getIndex();
		Log.i("listaItens", currComponent.toString());
		firstRecord = true;
		builder.runQuery(currComponent);
	}
	
	public void listaItens(Component component){
		componentStack.add(currComponent);
		currComponent = component;
		listaItens();
	}
	
	public void onRecord(Cursor cursor, Component component){
		if(firstRecord){
			areaCol = builder.getAreaCol();
			grupoCol = builder.getGrupoCol();
			processoCol = builder.getProcessoCol();
			artefatoCol = builder.getArtefatoCol();
			areaIdCol = builder.getAreaIdCol();
			grupoIdCol = builder.getGrupoIdCol();
			processoIdCol = builder.getProcessoIdCol();
			artefatoIdCol = builder.getArtefatoIdCol();
			aplicacaoIdCol = builder.getAplicacaoIdCol();
		}
		switch(component.getType()){
			case Component.AREA:
				mostraArea(cursor);
				break;
			case Component.GRUPO:
				mostraGrupo(cursor);
				break;
			case Component.PROCESSO:
				mostraProcesso(cursor);
				break;
			case Component.ARTEFATO:
				mostraArtefato(cursor);
				break;
		}
	}
	
	public void afterLastRecord(){
		viewGroups.clear();
	}
	
	private void mostraArea(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			Log.i("mostraArea", cursor.getString(areaCol));
			setTitle(cursor.getString(areaCol));
			//itens.addView(new ItemTextView(this, cursor.getString(areaCol), null));
		}
		String strGrupo = cursor.getString(grupoCol);
		ViewGroup viewGroup = getGroup(strGrupo, cursor, Component.GRUPO, grupoIdCol);
		TextView tv = new ItemTextView(this, cursor.getString(processoCol), new Component(cursor.getInt(processoIdCol), Component.PROCESSO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}

	private ViewGroup getGroup(String strGrupo)
	{
		LinearLayout viewGroup = (LinearLayout) viewGroups.get(strGrupo);
		if (viewGroup == null)
		{
			itens.addView(new ItemTextView(this, strGrupo, null));
			viewGroup = new LinearLayout(this);
			viewGroup.setOrientation(LinearLayout.VERTICAL);
			viewGroups.put(strGrupo, viewGroup);
			itens.addView(viewGroup);
		}
		return viewGroup;
	}

	private ViewGroup getGroup(String strGrupo, Cursor cursor, int tipo, int idCol)
	{
		LinearLayout viewGroup = (LinearLayout) viewGroups.get(strGrupo);
		if (viewGroup == null)
		{
			itens.addView(new ItemTextView(this, strGrupo, new Component(cursor.getInt(idCol), tipo)));
			viewGroup = new LinearLayout(this);
			viewGroup.setOrientation(LinearLayout.VERTICAL);
			viewGroups.put(strGrupo, viewGroup);
			itens.addView(viewGroup);
		}
		return viewGroup;
	}
	
	private void mostraGrupo(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			setTitle(cursor.getString(grupoCol));
			//itens.addView(new ItemTextView(this, cursor.getString(grupoCol), null));
		}
		String strArea = cursor.getString(areaCol);
		ViewGroup viewGroup = getGroup(strArea, cursor, Component.AREA, areaIdCol);
		TextView tv = new ItemTextView(this, cursor.getString(processoCol), new Component(cursor.getInt(processoIdCol), Component.PROCESSO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}
	
	private void mostraProcesso(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			setTitle(cursor.getString(processoCol));
			//itens.addView(new ItemTextView(this, cursor.getString(processoCol), null));
			itens.addView(new ItemTextView(this, cursor.getString(areaCol), new Component(cursor.getInt(areaIdCol), Component.AREA)));
			itens.addView(new ItemTextView(this, cursor.getString(grupoCol), new Component(cursor.getInt(grupoIdCol), Component.GRUPO)));
		}
		String strAplicacao = APLICACOES[ cursor.getInt(aplicacaoIdCol)-1];
		ViewGroup viewGroup = getGroup(strAplicacao);
		TextView tv = new ItemTextView(this, cursor.getString(artefatoCol), new Component(cursor.getInt(artefatoIdCol), Component.ARTEFATO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}
	
	private void mostraArtefato(Cursor cursor){
		if(firstRecord){
			firstRecord = false;
			setTitle(cursor.getString(artefatoCol));
			//itens.addView(new ItemTextView(this, cursor.getString(artefatoCol), null));
		}
		String strAplicacao = APLICACOES_ARTEFATO[ cursor.getInt(aplicacaoIdCol)-1];
		Log.i("Artefato", strAplicacao);
		ViewGroup viewGroup = getGroup(strAplicacao);
		TextView tv = new ItemTextView(this, cursor.getString(processoCol), new Component(cursor.getInt(processoIdCol), Component.PROCESSO));
		tv.setPadding(10, 0, 0, 0);
		viewGroup.addView(tv);
	}
	
	@Override
	public void onBackPressed() {
		if (!componentStack.isEmpty()) {
			currComponent = componentStack.pop();
			listaItens();
			return;
		}

		// Otherwise defer to system default behavior.
		super.onBackPressed();
	}
}
