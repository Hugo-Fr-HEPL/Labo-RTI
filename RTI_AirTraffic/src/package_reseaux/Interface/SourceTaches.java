package package_reseaux.Interface;

public interface SourceTaches {
    public Runnable getTache();
    public void recordTache (Runnable r);
}