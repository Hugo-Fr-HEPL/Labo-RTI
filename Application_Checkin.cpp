#include "reseaux.h"

#define DOC "DENNY_OF_CONNEXION"
#define EOC "END_OF_CONNEXION"

int main(void)
{
	int ip;
	struct sockaddr_in adresse;
	properties prop = Load_Properties(FILENAME);
	struct in_addr adresseIP;

	char msgClient[MAXSTRING] = "bonjour petite peruche";
	char msgServeur[MAXSTRING];

	ip = Create_Socket(AF_INET, SOCK_STREAM, 0);

	adresse = Infos_Host(prop);
	adresseIP = adresse.sin_addr;

	Connect_Client(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	do
	{
	
		Send_Message(ip, msgClient, MAXSTRING, 0);
		
		if(strcmp(msgClient,EOC))
		{
			Receive_Message(ip, msgServeur, MAXSTRING, 0);
		}
	}while(strcmp(msgClient,EOC)&&strcmp(msgServeur,DOC));

	return EXIT_SUCCESS;
}
