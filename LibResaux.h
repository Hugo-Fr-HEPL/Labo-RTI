#include <stdlib.h>
#include <stdio.h>
#include <string.h>

int Create_Socket(int ipaddr, int type, int protocole);
void Bind_Socket(int handle, const struct sockaddr *adresse, int taille);
void Listen_Serveur(int handle, int nbrconnection);
void Connect_Client(int handle, struct sockaddr *adresse, int taille);
void Accept_Serveur(int handle, struct sockaddr *adresse, int taille);
