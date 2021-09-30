#include "reseaux.h"

int main(void)
{
	int ip;
	properties prop;
	struct sockaddr_in adresse;
	struct in_addr adresseIP;
	char msgClient[MAXSTRING] = "bonjour petite peruche";

	ip = Create_Socket(AF_INET, SOCK_STREAM, 0);


	prop = Load_Properties("properties.txt");

	struct hostent *infosHost;
	if((infosHost = gethostbyname(prop.nomMachine))==0)
	{
		printf("Erreur d'acquisition d'infos sur le host %d \n",errno);
		exit(1);
	}
	memcpy(&adresseIP, infosHost->h_addr, infosHost->h_length);

	memset(&adresse, 0, sizeof(struct sockaddr_in));
	adresse.sin_family = AF_INET;
	adresse.sin_port = htons(prop.port);
	memcpy(&adresse.sin_addr, infosHost->h_addr, infosHost->h_length);

	Connect_Client(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	Send_Message(ip, msgClient, MAXSTRING, 0);

	return 0;
}
