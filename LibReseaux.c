#include "LibReseaux.h"

int Create_Socket(int ipaddr, int type, int protocole)
{
	int handsock;
	handsock = socket(ipaddr,type,protocole);
	if(handsock==-1)
	{
		printf("Erreur de creation de la socket %d \n",errno);
		exit(1);
	}
	printf("socket n°: %d \n",handsock);
	return handsock;
}
void Bind_Socket(int handle, const struct sockaddr *adresse, int taille)
{
	int reussite;
	reussite = bind(handle, adresse, taille);
	if(reussite==-1)
	{
		printf("Erreur sur le bind de la socket %d \n",errno);
		exit(1);
	}
}
void Listen_Serveur(int handle, int nbrconnexion)
{
	int reussite;
	reussite = listen(handle, nbrconnexion);
	if(reussite==-1)
	{
		printf("Erreur sur l'ecoute %d \n",errno);
		//close(handle);
		exit(1);
	}
}
void Connect_Client(int handle, struct sockaddr *adresse, int taille)
{
	int reussite;
	reussite = connect(handle, adresse, taille);
	if(reussite==-1)
	{
		printf("Erreur de connexion %d \n",errno);
		//close(handle);
		exit(1);
	}
}
int Accept_Serveur(int handle, struct sockaddr *adresse, int taille)
{
	int HSocketService;
	unsigned int t = taille;
	HSocketService = accept(handle,adresse,&t);
	if(HSocketService==-1)
	{
		printf("Erreur sur l'acceptation %d \n",errno);
		//close(handle);
		exit(1);
	}
	return HSocketService;
}
void Send_Message(int handleCible, const void* message, int taillemessage, int flagUrg)
{
	int reussite;
	reussite = send(handleCible, message, taillemessage, flagUrg);
	if(reussite==-1)
	{
		printf("Erreur sur le send %d \n",errno);
		exit(1);
	}
}
void Receive_Message(int handleSource, void* message, int taillemessage, int flagUrgdest)
{
	int reussite;
	reussite = recv(handleSource, message, taillemessage, flagUrgdest);
	if(reussite==-1)
	{
		printf("Erreur sur le receive %d \n",errno);
		exit(1);
	}
	printf("message recu: %s\n",message)
}
