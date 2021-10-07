#include <pthread.h>

#include "reseaux.h"


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

int indiceCourant = -1;
