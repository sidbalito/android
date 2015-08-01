
package com.sidbatista.relationalpmbok5;
import android.database.*;
interface ItensList
{
	public void listaItens(Component component);
	public void onRecord(Cursor cursor, Component component);
	public void afterLastRecord();
}
