package package_reseaux.other;

import package_reseaux.Interface.SourceTaches;

public class ThreadClient extends Thread {
    private SourceTaches tachesAExecuter;

    Runnable tacheEnCours;

    public ThreadClient(SourceTaches st) {
        tachesAExecuter = st;
    }

    public void run() {
            System.out.println("Thread client avant get");
            tacheEnCours = tachesAExecuter.getTache();
                
            System.out.println("Tache en cours");
            tacheEnCours.run();
    }
}
