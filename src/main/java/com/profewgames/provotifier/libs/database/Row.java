package com.profewgames.provotifier.libs.database;

import xyz.ufactions.database.column.Column;
import xyz.ufactions.libs.NautHashMap;

public class Row
{
	public NautHashMap<String, Column<?>> Columns = new NautHashMap<String, Column<?>>();
}
