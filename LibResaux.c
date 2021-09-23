#include "LibResaux.h"

int Create_Socket(int ipaddr, int type, int protocole)
{
	int handsock;
	handsock = socket(ipaddr,type,protocole);
	if(handsock==-1)
	{
		printf("Erreur de creation de la socket %d \n",errno);
		exit(1);
	}
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
void Listen_Serveur(int handle, int nbrconnection)
{
	int reussite;
	reussite = listen(handle, nbrconnection);
	if(reussite==-1)
	{
		printf("Erreur sur l'ecoute %d \n",errno);
		close(handle);
		exit(1);
	}
}
void Connect_Client(int handle, struct sockaddr *adresse, int taille)
{
	int reussite;
	reussite = connect(handle, adresse, taille);
	if(reussite==-1)
	{
		printf("Erreur de connection %d \n",errno);
		close(handle);
		exit(1);
	}
}
void Accept_Serveur(int handle, struct sockaddr *adresse, int taille)
{
	int reussite;
	reussite = accept(handle,adresse,taille);
	if(reussite==-1)
	{
		printf("Erreur sur l'acceptation %d \n",errno);
		close(handle);
		exit(1);
	}
	
}
