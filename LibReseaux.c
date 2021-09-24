#include "LibReseaux.h"


int Create_Socket(int ipaddr, int type, int protocole)
{
	int handsock = socket(ipaddr, type, protocole);
	if(handsock == -1)
	{
		printf("Erreur de creation de la socket %d \n", errno);
		exit(1);
	}
	printf("socket n°: %d \n", handsock);

	return handsock;
}

void Bind_Socket(int handle, const struct sockaddr* adresse, int taille)
{
	int reussite = bind(handle, adresse, taille);
	if(reussite == -1)
	{
		printf("Erreur sur le bind de la socket %d \n", errno);
		exit(1);
	}
}

void Infos_Host(struct sockaddr_in* adresse)
{
	struct hostent* infosHost;
	if((infosHost = gethostbyname("Solaris11DM2019")) == 0)
	{
		printf("Erreur d'acquisition d'infos sur le host %d \n", errno);
		exit(1);
	}
	adresse->sin_family = AF_INET;
	adresse->sin_port = htons(50000);
	memcpy(&adresse->sin_addr, infosHost->h_addr, infosHost->h_length);
}



void Listen_Server(int handle, int nbrconnexion)
{
	int reussite = listen(handle, nbrconnexion);
	if(reussite == -1)
	{
		printf("Erreur sur l'ecoute %d \n", errno);
		#ifdef SUN
			close(handle);
		#endif
		#ifndef SUN
			closesocket(handle);
		#endif
		exit(1);
	}
}

int Accept_Server(int handle, struct sockaddr* adresse, int taille)
{
	//unsigned int t = taille;
	int HSocketService = accept(handle, adresse, &taille);
	if(HSocketService == -1)
	{
		printf("Erreur sur l'acceptation %d \n", errno);
		#ifdef SUN
			close(handle);
		#endif
		#ifndef SUN
			closesocket(handle);
		#endif
		exit(1);
	}

	return HSocketService;
}



void Connect_Client(int handle, struct sockaddr* adresse, int taille)
{
	int reussite = connect(handle, adresse, taille);
	if(reussite == -1)
	{
		printf("Erreur de connexion %d \n", errno);
		#ifdef SUN
			close(handle);
		#endif
		#ifndef SUN
			closesocket(handle);
		#endif
		exit(1);
	}
}



void Send_Message(int handleCible, const void* message, int taillemessage, int flagUrg)
{
	int reussite = send(handleCible, message, taillemessage, flagUrg);
	if(reussite == -1)
	{
		printf("Erreur sur le send %d \n", errno);
		exit(1);
	}
}

void Receive_Message(int handleSource, void* message, int taillemessage, int flagUrgdest)
{
	int reussite = recv(handleSource, message, taillemessage, flagUrgdest);
	if(reussite == -1)
	{
		printf("Erreur sur le receive %d \n", errno);
		exit(1);
	}
	//printf("message recu: %s\n", message);
}
