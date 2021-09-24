#include "LibReseaux.h"

int main()
{
	int hSocketEcoute, hSocketService;
	struct sockaddr_in adresse;

	hSocketEcoute = Create_Socket(AF_INET, SOCK_STREAM, 0);

	Infos_Host(&adresse);

	Bind_Socket(hSocketEcoute, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	Listen_Server(hSocketEcoute, SOMAXCONN);

	hSocketService = Accept_Server(hSocketEcoute, (struct sockaddr*)&adresse, sizeof(struct sockaddr_in));

	return EXIT_SUCCESS;
}
