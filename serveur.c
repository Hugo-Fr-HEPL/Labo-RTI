#include "LibReseaux.h"

int main(void)
{
	int ip, HSocketService;
	struct sockaddr_in adresse;

	ip = Create_Socket(AF_INET, SOCK_STREAM, 0);

	struct hostent *infosHost;
	if((infosHost = gethostbyname("Solaris11DM2019"))==0)
	{
		printf("Erreur d'acquisition d'infos sur le host %d \n",errno);
		exit(1);
	}
	adresse.sin_family = AF_INET;
	adresse.sin_port = htons(50000);
	memcpy(&adresse.sin_addr,infosHost->h_addr,infosHost->h_length);

	Bind_Socket(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	Listen_Serveur(ip, SOMAXCONN);

	HSocketService=Accept_Serveur(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	return 0;
}
