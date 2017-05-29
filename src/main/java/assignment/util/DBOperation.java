package assignment.util;


public class DBOperation<T> {

    protected CacheEngine.DataGetter<T> dataGetter;
    protected CacheEngine.DataSetter<T> dataSetter;

    public DBOperation(CacheEngine.DataGetter<T> dataGetter, CacheEngine.DataSetter<T> dataSetter) {
        this.dataGetter = dataGetter;
        this.dataSetter = dataSetter;
    }
}