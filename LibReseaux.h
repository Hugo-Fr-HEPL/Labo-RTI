#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <netdb.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>

#include <fstream>
#include <unistd.h>
#include <iostream>

#define MAXSTRING 100

typedef struct properties {
	char* nomMachine = NULL;
	int port;
} properties;

int Create_Socket(int ipaddr, int type, int protocole);
void Bind_Socket(int handle, const struct sockaddr *adresse, int taille);
void Listen_Serveur(int handle, int nbrconnection);
void Connect_Client(int handle, struct sockaddr *adresse, int taille);
int Accept_Serveur(int handle, struct sockaddr *adresse, int taille);
void Send_Message(int handleCible, const void* message, int taillemessage, int flagUrg);
void Receive_Message(int handleSource, void* message, int taillemessage, int flagUrgdest);

properties Load_Properties(const char* nomFichier);
void Read_Lines(int ligne, char* txt, char* ret);
