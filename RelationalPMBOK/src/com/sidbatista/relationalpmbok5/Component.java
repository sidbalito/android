package com.sidbatista.relationalpmbok5;

public class Component 
{
	private int index;
	private int type;
	
	public static final String
		TYPE="component.type",
		INDEX="component.index";
	
	public static final int
		GRUPO = 1,
		AREA = 2,
		PROCESSO = 3,
		ARTEFATO = 4;
		
	

	public Component(int index, int type)
	{
		this.index = index;
		this.type = type;
	}

	public int getIndex()
	{
		return index;
	}

	public int getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Component(");
		sb.append(index).append(", ").append(type).append(")");
		return sb.toString();
	}
	
}
