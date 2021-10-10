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
bool VerifLogin(int socket);
bool VerifTicket(int socket);
char* VerifLuggage(int socket);
bool PaymentDone(int socket, char* bag);


pthread_mutex_t mutexIndiceCourant;
pthread_cond_t condIndiceCourant;


SocketServer sock;
int indiceCourant = -1;

#endif