#include <pthread.h>

#include "reseaux.h"

#define DOC "DENY_OF_CONNEXION"
#define EOC "END_OF_CONNEXION"

#define affThread(num, msg) printf("th_%s > %s\n", num, msg)

typedef struct paramThread {
    int i;
    int* hSocketConnectee;
} paramThread;


void* fctThread(void* param);
char* getThreadIdentity();


pthread_mutex_t mutexIndiceCourant;
pthread_cond_t condIndiceCourant;

int indiceCourant = -1;


int main() {
	int hSocketEcoute, hSocketService;
	struct sockaddr_in adresse;
	properties prop = Load_Properties(FILENAME);
    pthread_t threadHandle[prop.nbServer];
    int hSocketConnectee[prop.nbServer];
    paramThread param;


// Initialisation des Threads
    pthread_mutex_init(&mutexIndiceCourant, NULL);
    pthread_cond_init(&condIndiceCourant, NULL);


	hSocketEcoute = Create_Socket(AF_INET, SOCK_STREAM, 0);

	adresse = Infos_Host(prop);

	Bind_Socket(hSocketEcoute, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));


// Pool de Threads pour le serveur
    for(int i = 0; i < prop.nbServer; i++) {
        param.i = i;
        param.hSocketConnectee = hSocketConnectee;
        pthread_create(&threadHandle[i], NULL, fctThread, (void*)&param);
        printf("Creation du thread %d\n", i);
        pthread_detach(threadHandle[i]);
    }


do {
	Listen_Server(hSocketEcoute, SOMAXCONN);

	hSocketService = Accept_Server(hSocketEcoute, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

    printf("Recherche d'un socket connectee libre ...\n");
    int i;
    for(i = 0; i < prop.nbServer && hSocketConnectee[i] != -1; i++);

    if(i == prop.nbServer) {
        printf("Plus de connexion disponible\n");

        char* msgServeur = NULL;
        sprintf(msgServeur, DOC);
        Send_Message(hSocketService, msgServeur, MAXSTRING, 0);
    } else {
        printf("Connexion sur la socket num %d\n", i);

        pthread_mutex_lock(&mutexIndiceCourant);

        hSocketConnectee[i] = hSocketService;
        indiceCourant = i;

        pthread_mutex_unlock(&mutexIndiceCourant);
        pthread_cond_signal(&condIndiceCourant);
    }

	//Receive_Message(hSocketService, msgClient, MAXSTRING, 0);
} while(1);

    close(hSocketEcoute);
    printf("Socket serveur ferme\n");

    puts("Fin du thread princhSocketEcouteal\n");
	return EXIT_SUCCESS;
}

void* fctThread(void* param) {
    int vr = ((paramThread*)param)->i;
    int* hSocketConnectee = ((paramThread*)param)->hSocketConnectee;

    char *buf = (char*)malloc(100);
    char msgClient[MAXSTRING], msgServeur[MAXSTRING];
    int finDialogue=0, iCliTraite;
    int retRecv;
    char * numThr = getThreadIdentity();
    int hSocketServ;

    while(1) {
//Attente d'un client à traiter
        pthread_mutex_lock(&mutexIndiceCourant);

        while(indiceCourant == -1)
            pthread_cond_wait(&condIndiceCourant, &mutexIndiceCourant);
        iCliTraite = indiceCourant;
        indiceCourant = -1;
        hSocketServ = *(hSocketConnectee+indiceCourant);

        pthread_mutex_unlock(&mutexIndiceCourant);
        sprintf(buf, "Je m'occupe du numero %d ...", iCliTraite);
        affThread(numThr, buf);


// Dialogue Thread-Client
        finDialogue = 0;
        do {
            if((retRecv = recv(hSocketServ, msgClient, MAXSTRING,0)) == -1) {
                printf("Erreur sur le recv de la socket connectee : %d\n", errno);
                close(hSocketServ);
                exit(1);
            } else if(retRecv == 0) {
                sprintf(buf, "Le client est parti !!!");
                affThread(numThr, buf);
                finDialogue = 1;
                break;
            } else {
                sprintf(buf, "Message recu = %s\n", msgClient);
                affThread(numThr, buf);
            }

            if(strcmp(msgClient, EOC) == 0) {
                finDialogue = 1;
                break;
            }

            sprintf(msgServeur, "ACK pour votre message : <%s>\n", msgClient);
            Send_Message(hSocketServ, msgServeur, MAXSTRING, 0);
        } while(!finDialogue);

        pthread_mutex_lock(&mutexIndiceCourant);
        hSocketConnectee[iCliTraite] = -1;
        pthread_mutex_unlock(&mutexIndiceCourant);
    }
    close(hSocketServ);
    return (void*)vr;
}

char* getThreadIdentity() {
    unsigned int numSequence;
    char* buf = (char*)malloc(30);

    //numSequence = pthread_getsequence_np(pthread_self);
    sprintf(buf, "%d.%u\n", getpid(), numSequence);

    return buf;
}