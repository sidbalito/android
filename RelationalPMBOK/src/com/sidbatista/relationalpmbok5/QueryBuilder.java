package com.sidbatista.relationalpmbok5;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;
import android.util.*;
import java.io.*; 

public class QueryBuilder extends SQLiteOpenHelper
{
	private static SQLiteDatabase base;
	private static QueryBuilder instance;
	private static String filePath;// = "/storage/sdcard0/Download/PMBOK.db";
	private static int version = 1;
	private static ItensList itens;
	private String[] campos;
	private int
		grupoCol, areaCol, processoCol, artefatoCol,
		grupoIdCol, areaIdCol, processoIdCol, artefatoIdCol, aplicacaoIdCol;
		
	private static final String
		AREA = "area",
		GRUPO = "grupo",
		PROCESSO = "processo",
		ARTEFATO = "artefato",
		GRUPO_ID = "grupoid",
		AREA_ID = "areaid",
		PROCESSO_ID = "processoid",
		ARTEFATO_ID = "artefatoid",
		APLICACAO_ID = "_aplicacao";
	
	private QueryBuilder(Context context){
		super(context, filePath, null, version);
		if(context instanceof ItensList) itens = (ItensList) context;
		//base = getReadableDatabase();
	}
	
	public static QueryBuilder newInstance(Context context){
		itens = (ItensList) context;
		if(filePath == null) resToFile(context);
		if(instance == null) instance = new QueryBuilder(context);
		return instance;
	}

	public int getGrupoCol()
	{
		return grupoCol;
	}

	public int getAreaCol()
	{
		return areaCol;
	}

	public int getProcessoCol()
	{
		return processoCol;
	}

	public int getArtefatoCol()
	{
		return artefatoCol;
	}

	public int getGrupoIdCol()
	{
		return grupoIdCol;
	}

	public int getAreaIdCol()
	{
		return areaIdCol;
	}

	public int getProcessoIdCol()
	{
		return processoIdCol;
	}

	public int getArtefatoIdCol()
	{
		return artefatoIdCol;
	}

	public int getAplicacaoIdCol()
	{
		return aplicacaoIdCol;
	}

	@Override
	public void onCreate(SQLiteDatabase p1){}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3){}
	
	private void getColumns(Cursor cursor){
		areaCol = cursor.getColumnIndex(AREA);
		grupoCol = cursor.getColumnIndex(GRUPO);
		processoCol = cursor.getColumnIndex(PROCESSO);
		artefatoCol = cursor.getColumnIndex(ARTEFATO);
		areaIdCol = cursor.getColumnIndex(AREA_ID);
		grupoIdCol = cursor.getColumnIndex(GRUPO_ID);
		processoIdCol = cursor.getColumnIndex(PROCESSO_ID);
		artefatoIdCol = cursor.getColumnIndex(ARTEFATO_ID);
		aplicacaoIdCol = cursor.getColumnIndex(APLICACAO_ID);
	}
	
	public void runQuery(int type){
		String query = null;
		switch(type){
			case Component.AREA:
				query = "select area, areaid from areas";
				break;
			case Component.GRUPO:
				query = "select grupo, grupoid from grupos";
				break;
			case Component.PROCESSO:
				break;
			case Component.ARTEFATO:
				break;
		}
		Cursor cursor = base().rawQuery(query, null);
		Log.i("query", query);
		//getColumns(cursor);
		Log.i("query", cursor.getCount()+" registros");
		Component component = new Component(0, type);
		while(!cursor.isLast()){
			cursor.moveToNext();
			itens.onRecord(cursor, component);
		}
		cursor.close();
		itens.afterLastRecord();
	}

	public void runQuery(Component component){
		int index = component.getIndex();
		String query = null;
		switch(component.getType()){
			case Component.AREA:
				query = buildQuery(
					new String[]{"area", "grupo", "processo", "grupoid", "processoid"},
					new String[]{"areas", "grupos", "processos"},
					new String[]{"_area", "grupoid", "areaid"},
					new Object[]{index, "_grupo", "_area"}
					);
				break;
			case Component.GRUPO:
				query = buildQuery(
					new String[]{"area", "grupo", "processo", "areaid", "processoid"},
					new String[]{"processos", "grupos", "areas"},
					new String[]{"_grupo", "grupoid", "areaid"},
					new Object[]{index, "_grupo", "_area"}
				);
				break;
			case Component.PROCESSO:
				query = buildQuery(
					new String[]{"area", "grupo", "processo", "artefato", "areaid", "artefatoid", "grupoid", "_aplicacao"},
					new String[]{"areas", "grupos", "processos", "artefatos", "estrutura"},
					new String[]{"processoid", "grupoid", "areaid", "artefatoid", "_processo"},
					new Object[]{index, "_grupo", "_area", "_artefato", "processoid"}
				);
				break;
			case Component.ARTEFATO:
				query = buildQuery(
					new String[]{"artefato", "processo", "_aplicacao", "processoid"},
					new String[]{"artefatos", "estrutura", "processos"},
					new String[]{"_artefato", "processoid", "artefatoid"},
					new Object[]{index, "_processo", "_artefato"}
				);
				break;
		}
		

		Cursor cursor = base().rawQuery(query, null);
		Log.i("query", query);
		getColumns(cursor);
		Log.i("query", cursor.getCount()+" registros");
		while(!cursor.isLast()){
			cursor.moveToNext();
			itens.onRecord(cursor, component);
		}
		cursor.close();
		itens.afterLastRecord();
	}
	
	private SQLiteDatabase base(){
		if(base == null|| !base.isOpen()){
			base = getReadableDatabase();
		}
		return base;
	}
	
	public String buildQuery(String[] campos, String[] tabelas, String[] criterios, Object[] valores){
		this.campos = campos;
		StringBuilder sb = new StringBuilder("select ");
		concatena(sb, campos, ", ");
		sb.append(" from ");
		concatena(sb, tabelas, ", ");
		sb.append(" where ");
		concatena(sb, criterios, "=", valores);
		return sb.toString();
	}
	
	private void concatena(StringBuilder sb, String[] strings, String separator){
		for(int i = 0; i < strings.length; i ++){
			if(i != 0) sb.append(separator);
			sb.append(strings[i]);
		}
	}
	
	private void concatena(StringBuilder sb, String[] criterios, String operador, Object[] valores){
		for(int i = 0; i < criterios.length; i++){
			if(i > 0) sb.append(" and ");
			sb.append(criterios[i]).append(operador);
			if(valores[i] instanceof String) sb.append(valores[i]);
			else sb.append(valores[i]);
		}
	}
	
	public void release(){
		base.close();
	}
	
	public static void resToFile(Context context){
		String url = null;
		try{
			filePath = context.getFilesDir().getPath()+"/base.db";
			
			url = filePath;
			File file = new File(url);
			if(file.exists()) return;
			InputStream is = context.getResources().openRawResource(R.raw.base);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			OutputStream os = new FileOutputStream(url);
			os.write(buffer);
			os.flush();
			os.close();
		} catch (Exception e){
			Log.e("resToFile", "Erro ao gravar arquivo "+url+"\n"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
}
