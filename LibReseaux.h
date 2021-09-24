#include <stdlib.h>
#include <stdio.h>
#include <string.h>
//#include <netdb.h>

#include <sys/types.h>
#ifdef SUN
    #include <sys/socket.h>
#endif
#ifndef SUN
    #include <winsock2.h>
#endif
#include <errno.h>

int Create_Socket(int ipaddr, int type, int protocole);
void Bind_Socket(int handle, const struct sockaddr* adresse, int taille);
void Infos_Host(struct sockaddr_in* adresse);

void Listen_Server(int handle, int nbrconnection);
int Accept_Server(int handle, struct sockaddr* adresse, int taille);

void Connect_Client(int handle, struct sockaddr* adresse, int taille);

void Send_Message(int handleCible, const void* message, int taillemessage, int flagUrg);
void Receive_Message(int handleSource, void* message, int taillemessage, int flagUrgdest);