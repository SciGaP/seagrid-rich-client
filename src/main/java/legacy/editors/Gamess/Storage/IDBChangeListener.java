package legacy.editors.gamess.Storage;

public interface IDBChangeListener 
{
	void DataAdded(String Data);
	void DataRemoved(String Data);
	void DropDB();
}
