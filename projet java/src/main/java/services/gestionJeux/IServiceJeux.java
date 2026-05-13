package services.gestionJeux;

import java.util.List;

public interface IServiceJeux<T> {
    void ajouter(T t);
    void modifier(T t);
    void supprimer(T t);
    List<T> getAll();
    T getById(int id);
}
