#include "LibReseaux.h"

int main(void)
{
	int ip, HSocketService;
	struct sockaddr_in adresse;
	properties prop;

	char msgClient[MAXSTRING];


	ip = Create_Socket(AF_INET, SOCK_STREAM, 0);


	prop = Load_Properties("properties.txt");

	struct hostent *infosHost;
	if((infosHost = gethostbyname(prop.nomMachine))==0)
	{
		printf("Erreur d'acquisition d'infos sur le host %d \n",errno);
		exit(1);
	}
	memset(&adresse, 0, sizeof(struct sockaddr_in));
	adresse.sin_family = AF_INET;
	adresse.sin_port = htons(prop.port);
	memcpy(&adresse.sin_addr,infosHost->h_addr,infosHost->h_length);

	Bind_Socket(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	Listen_Server(ip, SOMAXCONN);

	HSocketService = Accept_Server(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	Receive_Message(HSocketService, msgClient, MAXSTRING, 0);

	return 0;
}
