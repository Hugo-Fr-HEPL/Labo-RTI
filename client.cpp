#include "reseaux.h"

int main(void)
{
	int ip;
	struct sockaddr_in adresse;
	properties prop = Load_Properties(FILENAME);
	struct in_addr adresseIP;

	char msgClient[MAXSTRING] = "bonjour";

	ip = Create_Socket(AF_INET, SOCK_STREAM, 0);

	adresse = Infos_Host(prop);
	adresseIP = adresse.sin_addr;

	Connect_Client(ip, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	Send_Message(ip, msgClient, MAXSTRING, 0);

	return EXIT_SUCCESS;
}
