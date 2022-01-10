#ifndef SERVEUR_CHECKIN_H
#define SERVEUR_CHECKIN_H

#include <pthread.h>

#include "socketServer.h"
#include "airport.h"

#define LOGFILE "./conf/login.csv"
#define TICFILE "./conf/billets.csv"
#define LUGFILE "./conf/362_22082017_lug.csv"


typedef struct paramThread {
    int i;
    int* hSocketConnectee;
} paramThread;


void* fctThread(void* param);
int VerifLogin(int socket);
int VerifTicket(int socket);
char* VerifLuggage(int socket);
int PaymentDone(int socket, char* bag);
void endCheckin(int hSocketServ);
void editCheckin(int hSocketServ);


pthread_mutex_t mutexIndiceCourant;
pthread_cond_t condIndiceCourant;

pthread_mutex_t mutexFile;


SocketServer sock;
int indiceCourant = -1;

static int numCheckinClose = 0;

#endif