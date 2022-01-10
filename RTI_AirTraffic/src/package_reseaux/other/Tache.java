package package_reseaux.other;

import java.util.*;

import package_reseaux.Interface.SourceTaches;

public class Tache implements SourceTaches {
    private Runnable Tache;

    public Tache() {
        Tache = null;
    }

    public synchronized Runnable getTache(){
        return (Runnable)Tache;
    }

    public synchronized void recordTache(Runnable r) {
        Tache = r;
    }
}
